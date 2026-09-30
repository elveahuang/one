package cc.wdev.platform.commons.utils;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * @author elvea
 */
@Slf4j
public abstract class StringUtils extends org.springframework.util.StringUtils {

    public static String nvl(String value) {
        return nvl(value, "");
    }

    public static String nvl(String value, String defaultValue) {
        return isNotEmpty(value) ? value.trim() : defaultValue;
    }

    public static boolean isEmpty(@Nullable String str) {
        return !hasText(str);
    }

    public static boolean isNotEmpty(@Nullable String str) {
        return hasText(str);
    }

    public static boolean isBlank(@Nullable String str) {
        return !hasText(str);
    }

    public static boolean isNotBlank(@Nullable String str) {
        return hasText(str);
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String simpleUuid() {
        return IdUtil.simpleUUID();
    }

    /**
     * 生成随机字符串，包含字母和数字
     *
     * @param count 字符串长度
     * @return 随机字符串
     */
    public static String randomString(int count) {
        return RandomStringUtils.secure().nextAlphanumeric(count);
    }

    /**
     * 生成随机字符串，仅包含字母
     *
     * @param count 字符串长度
     * @return 随机字符串
     */
    public static String randomAlphabetic(int count) {
        return RandomStringUtils.secure().nextAlphabetic(count);
    }

    /**
     * 生成随机字符串，仅包含数字
     *
     * @param count 字符串长度
     * @return 随机字符串
     */
    public static String randomNumeric(int count) {
        return RandomStringUtils.secure().nextNumeric(count);
    }

    /**
     * 转大写
     */
    public static String toUpperCase(String text) {
        return isNotEmpty(text) ? text.toUpperCase() : null;
    }

    /**
     * 转小写
     */
    public static String toLowerCase(String text) {
        return isNotEmpty(text) ? text.toLowerCase() : null;
    }

    public static boolean startsWith(String text, String prefix) {
        return isNotEmpty(text) && text.startsWith(prefix);
    }

    public static boolean endsWith(String text, String suffix) {
        return isNotEmpty(text) && text.endsWith(suffix);
    }

    public static boolean equals(String str1, String str2) {
        return Strings.CS.equals(str1, str2);
    }
}
