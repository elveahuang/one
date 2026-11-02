package cc.wdev.platform.system.commons.util;

import cc.wdev.platform.commons.utils.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 钉钉 / 飞书（Lark）开放平台回调的验签与报文解密工具。
 *
 * <p>两家的回调加密协议一致（业界标准「AES-256-CBC + PKCS#7 填充 + 首 16 字节 IV」）：</p>
 * <ol>
 *   <li>{@code key = Base64Decode(EncodingAESKey + "=")}，得到 32 字节 AES 密钥。</li>
 *   <li>密文先 Base64 解码，前 16 字节是 IV，其余为密文体。</li>
 *   <li>解密后按 PKCS#7 去掉填充；再按 4 字节大端长度前缀切出 {@code msg_len}，其后是明文消息，
 *       再往后是 {@code appKey}。</li>
 * </ol>
 *
 * <p>飞书事件订阅的签名是 {@code SHA-256(timestamp + nonce + EncryptKey + body)} 的十六进制串，
 * 需与请求头/回调解构出的 {@code signature} 比对。</p>
 *
 * <p>工具不依赖任何第三方 SDK，便于在缺少 event 专用包的部署里独立使用。</p>
 *
 * @author elvea
 */
public abstract class CallbackCryptoUtils {

    /**
     * AES 分组长度（IV 长度）
     */
    private static final int IV_SIZE = 16;

    /**
     * 明文前的消息长度前缀字节数
     */
    private static final int MSG_LEN_SIZE = 4;

    /**
     * 解密开放平台回调密文，返回明文消息体。
     *
     * @param encryptKey 回调配置里的 EncodingAESKey / EncryptKey（43 位 Base64 变体）
     * @param encrypted  回调体中的 {@code encrypt} 字段值
     */
    public static String decrypt(String encryptKey, String encrypted) {
        if (StringUtils.isEmpty(encryptKey) || StringUtils.isEmpty(encrypted)) {
            throw new IllegalArgumentException("Callback encrypt key or payload is empty");
        }
        try {
            byte[] aesKey = Base64.getDecoder().decode(encryptKey + "=");
            byte[] cipherBytes = Base64.getDecoder().decode(encrypted);

            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(cipherBytes, 0, iv, 0, IV_SIZE);
            byte[] payload = new byte[cipherBytes.length - IV_SIZE];
            System.arraycopy(cipherBytes, IV_SIZE, payload, 0, payload.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(payload);

            // 去 PKCS#7 填充：最后一字节即填充长度
            int pad = decrypted[decrypted.length - 1] & 0xFF;
            if (pad < 1 || pad > 32) {
                throw new IllegalArgumentException("Invalid PKCS#7 padding");
            }
            int contentLength = decrypted.length - pad;
            // 前 4 字节为大端消息长度
            int msgLen = ((decrypted[0] & 0xFF) << 24)
                | ((decrypted[1] & 0xFF) << 16)
                | ((decrypted[2] & 0xFF) << 8)
                | (decrypted[3] & 0xFF);
            if (msgLen < 0 || msgLen > contentLength - MSG_LEN_SIZE) {
                throw new IllegalArgumentException("Invalid message length in decrypted payload");
            }
            return new String(decrypted, MSG_LEN_SIZE, msgLen, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Decrypt callback payload failed", e);
        }
    }

    /**
     * 飞书事件订阅签名：{@code SHA256(timestamp + nonce + encryptKey + body)} 的十六进制小写串。
     *
     * @param body 原始请求体（密文 JSON 原文，非解析后重组）
     */
    public static String larkSignature(String timestamp, String nonce, String encryptKey, String body) {
        String raw = StringUtils.nvl(timestamp) + StringUtils.nvl(nonce) + StringUtils.nvl(encryptKey) + StringUtils.nvl(body);
        return sha256Hex(raw);
    }

    /**
     * 常量时间比较签名，避免时序侧信道
     */
    public static boolean safeEquals(String expected, String actual) {
        if (StringUtils.isEmpty(expected) || StringUtils.isEmpty(actual)) {
            return false;
        }
        return MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8),
            actual.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 钉钉回调签名：{@code Base64(HmacSHA256(timestamp + "\n" + appSecret))}
     */
    public static String dingTalkSignature(String timestamp, String appSecret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder()
                .encodeToString(mac.doFinal((StringUtils.nvl(timestamp) + "\n").getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Compute dingtalk signature failed", e);
        }
    }

    private static String sha256Hex(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Compute SHA-256 failed", e);
        }
    }

}
