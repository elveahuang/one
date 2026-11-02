package cc.wdev.platform.system.commons.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * 钉钉 / 飞书回调加解密与验签单元测试。
 *
 * <p>用一个独立的「服务端」实现按开放平台协议加密，再验证工具类能正确解出明文，
 * 避免只测自家实现的对称性错误。</p>
 *
 * @author elvea
 */
class CallbackCryptoUtilsTests {

    /**
     * 43 位 Base64 变体密钥（与开放平台 EncodingAESKey 等长）
     */
    private static final String ENCRYPT_KEY = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG";

    private static final String APP_KEY = "dingappkey123";

    /**
     * 按开放平台协议加密：msg_len(4B) + msg + appKey，PKCS#7 填充，IV 前置
     */
    private static String encrypt(String encryptKey, String message, String appKey) throws Exception {
        byte[] aesKey = Base64.getDecoder().decode(encryptKey + "=");
        byte[] msg = message.getBytes(StandardCharsets.UTF_8);
        byte[] appKeyBytes = appKey.getBytes(StandardCharsets.UTF_8);

        byte[] plain = new byte[4 + msg.length + appKeyBytes.length];
        plain[0] = (byte) (msg.length >> 24);
        plain[1] = (byte) (msg.length >> 16);
        plain[2] = (byte) (msg.length >> 8);
        plain[3] = (byte) msg.length;
        System.arraycopy(msg, 0, plain, 4, msg.length);
        System.arraycopy(appKeyBytes, 0, plain, 4 + msg.length, appKeyBytes.length);

        // PKCS#7 填充补齐到 32 字节块
        int blockSize = 32;
        int pad = blockSize - (plain.length % blockSize);
        byte[] padded = Arrays.copyOf(plain, plain.length + pad);
        for (int i = plain.length; i < padded.length; i++) {
            padded[i] = (byte) pad;
        }

        byte[] iv = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(padded);

        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(result);
    }

    @Test
    void testDecryptRoundTrip() throws Exception {
        String message = "{\"EventType\":\"user_add_org\",\"UserId\":[\"123\"]}";
        String encrypted = encrypt(ENCRYPT_KEY, message, APP_KEY);

        Assertions.assertEquals(message, CallbackCryptoUtils.decrypt(ENCRYPT_KEY, encrypted));
    }

    @Test
    void testDecryptChineseContent() throws Exception {
        String message = "{\"text\":\"你好，世界\"}";
        String encrypted = encrypt(ENCRYPT_KEY, message, APP_KEY);

        // UTF-8 长度按字节计算，必须正确还原中文
        Assertions.assertEquals(message, CallbackCryptoUtils.decrypt(ENCRYPT_KEY, encrypted));
    }

    @Test
    void testDecryptWithWrongKeyFails() throws Exception {
        String encrypted = encrypt(ENCRYPT_KEY, "{\"a\":1}", APP_KEY);
        String wrongKey = "ZYXWVUTSRQPONMLKJIHGFEDCBA9876543210zyxwvut";

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> CallbackCryptoUtils.decrypt(wrongKey, encrypted));
    }

    @Test
    void testDecryptRejectsEmptyInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CallbackCryptoUtils.decrypt("", "abc"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> CallbackCryptoUtils.decrypt(ENCRYPT_KEY, ""));
    }

    @Test
    void testLarkSignatureIsStable() {
        String sig1 = CallbackCryptoUtils.larkSignature("1700000000", "nonce123", ENCRYPT_KEY, "{\"encrypt\":\"x\"}");
        String sig2 = CallbackCryptoUtils.larkSignature("1700000000", "nonce123", ENCRYPT_KEY, "{\"encrypt\":\"x\"}");

        Assertions.assertEquals(sig1, sig2);
        // SHA-256 十六进制固定 64 位
        Assertions.assertEquals(64, sig1.length());
    }

    @Test
    void testLarkSignatureChangesWithBody() {
        String sig1 = CallbackCryptoUtils.larkSignature("1700000000", "n", ENCRYPT_KEY, "{\"a\":1}");
        String sig2 = CallbackCryptoUtils.larkSignature("1700000000", "n", ENCRYPT_KEY, "{\"a\":2}");

        Assertions.assertNotEquals(sig1, sig2);
    }

    @Test
    void testDingTalkSignatureIsHmacBase64() {
        String sig = CallbackCryptoUtils.dingTalkSignature("1700000000", "secret");

        Assertions.assertNotNull(sig);
        // HmacSHA256 的 Base64 结果固定 44 字符（含 1 个 = 填充）
        Assertions.assertEquals(44, sig.length());
        Assertions.assertTrue(sig.endsWith("="));
    }

    @Test
    void testSafeEquals() {
        Assertions.assertTrue(CallbackCryptoUtils.safeEquals("abc", "abc"));
        Assertions.assertFalse(CallbackCryptoUtils.safeEquals("abc", "abd"));
        Assertions.assertFalse(CallbackCryptoUtils.safeEquals("abc", ""));
        Assertions.assertFalse(CallbackCryptoUtils.safeEquals("", "abc"));
        Assertions.assertFalse(CallbackCryptoUtils.safeEquals(null, "abc"));
    }

}
