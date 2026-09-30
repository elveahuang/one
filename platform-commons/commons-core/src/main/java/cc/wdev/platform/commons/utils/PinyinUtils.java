package cc.wdev.platform.commons.utils;

import cn.hutool.extra.pinyin.PinyinUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 拼音工具类
 */
public class PinyinUtils {
    private static final Map<String, String> SPECIAL_REGION_MAP = new HashMap<>();

    static {
        // 多音字地名：核心映射（按需添加，覆盖所有常见多音字地名）
        SPECIAL_REGION_MAP.put("重庆市", "C");    // 重：chóng → C
        SPECIAL_REGION_MAP.put("厦门市", "X");    // 厦：xià → X（部分引擎误判为shà）
        SPECIAL_REGION_MAP.put("丽水市", "L");    // 丽：lí → L（部分引擎误判为lì）
        SPECIAL_REGION_MAP.put("台州市", "T");    // 台：tāi → T（部分引擎误判为tái）
        SPECIAL_REGION_MAP.put("六安市", "L");    // 六：lù → L（部分引擎误判为liù）
        SPECIAL_REGION_MAP.put("番禺区", "P");    // 番：pān → P（部分引擎误判为fān）
    }

    /**
     * 获取汉字的拼音首字母（大写，适配前端IndexBar）
     *
     * @param chinese 地区名称（如“北京市”“重庆市”）
     * @return 首字母（如“B”“C”），无汉字则返回“#”
     */
    public static String getFirstLetterUpper(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return "#";
        }

        if (SPECIAL_REGION_MAP.containsKey(chinese)) {
            return SPECIAL_REGION_MAP.get(chinese);
        }

        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (!PinyinUtil.isChinese(c)) {
                continue;
            }
            try {
                char firstChar = PinyinUtil.getFirstLetter(c);
                String firstLetter = String.valueOf(firstChar).toUpperCase();
                // 过滤声调字符（若引擎返回带声调的首字母，如“b̄”，则取纯字母）
                firstLetter = filterTone(firstLetter);
                return StringUtils.isAlphanumeric(firstLetter) ? firstLetter : "#";
            } catch (Exception e) {
                return "#";
            }
        }
        return "#";
    }

    /**
     * 过滤拼音中的声调字符（如“ā”→“a”，“b̄”→“b”）
     *
     * @param letter 带声调的字母
     * @return 纯字母（大写）
     */
    private static String filterTone(String letter) {
        if (StringUtils.isBlank(letter)) {
            return "#";
        }
        // 匹配a-z/A-Z的字母，取第一个匹配的字符
        return letter.replaceAll("[^a-zA-Z]", "").toUpperCase();
    }
}
