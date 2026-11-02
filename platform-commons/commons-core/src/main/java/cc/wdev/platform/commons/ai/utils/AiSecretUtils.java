package cc.wdev.platform.commons.ai.utils;

import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import cc.wdev.platform.commons.utils.StringUtils;

/**
 * AI 密钥存储工具：落库前 AES 加密，读取时解密；兼容历史明文（无前缀原样返回）
 *
 * @author elvea
 */
public abstract class AiSecretUtils {

    private static final String PREFIX = "enc:";

    /**
     * 加密存储值
     */
    public static String encrypt(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return PREFIX + SensitiveUtils.doAesEncrypt(value);
    }

    /**
     * 解密存储值
     */
    public static String decrypt(String value) {
        if (StringUtils.isEmpty(value) || !value.startsWith(PREFIX)) {
            return value;
        }
        try {
            return SensitiveUtils.doAesDecrypt(value.substring(PREFIX.length()));
        } catch (Exception e) {
            return value;
        }
    }

}
