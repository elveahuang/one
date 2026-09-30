package cc.wdev.platform.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author elvea
 */
public abstract class RegexUtils {

    /**
     * 验证邮箱
     *
     * @param email email地址
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email) {
        String regex = "^([A-Za-z0-9_\\-.])+@([A-Za-z0-9_\\-.])+\\.([A-Za-z]{2,4})$";
        return Pattern.matches(regex, email);
    }

    /**
     * 验证手机号码
     *
     * @param mobile 手机号码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3-9]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 检测是否包含中文字符
     */
    public boolean containsChinese(String text) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    /**
     * 检测是否是中文字符
     */
    public boolean checkChinese(String text) {
        int n = 0;
        for (int i = 0; i < text.length(); i++) {
            n = (int) text.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

}
