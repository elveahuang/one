package cc.wdev.platform.commons.extensions.sensitive;

import cc.wdev.platform.commons.utils.StringUtils;
import cn.hutool.core.util.DesensitizedUtil;

/**
 * @author elvea
 */
public abstract class SensitiveUtils {

    /**
     * AES 加密
     */
    public static String doAesEncrypt(String value) {
        return SensitiveManager.getService().encrypt(value);
    }

    /**
     * AES 加密
     */
    public static String doAesDecrypt(String value) {
        return SensitiveManager.getService().decrypt(value);
    }

    /**
     * 密钥
     */
    public static String apiKey(String apiKey) {
        return StringUtils.isNotEmpty(apiKey) ? DesensitizedUtil.idCardNum(apiKey, 5, 2) : "";
    }

    /**
     * 身份证脱敏
     */
    public static String idCard(String idCard) {
        return StringUtils.isNotEmpty(idCard) ? DesensitizedUtil.idCardNum(idCard, 1, 2) : idCard;
    }

    /**
     * 手机号码脱敏
     */
    public static String mobileNumber(String mobileNumber) {
        return StringUtils.isNotEmpty(mobileNumber) ? DesensitizedUtil.mobilePhone(mobileNumber) : mobileNumber;
    }

    /**
     * 电子邮箱脱敏
     */
    public static String email(String email) {
        return StringUtils.isNotEmpty(email) ? DesensitizedUtil.email(email) : email;
    }

}
