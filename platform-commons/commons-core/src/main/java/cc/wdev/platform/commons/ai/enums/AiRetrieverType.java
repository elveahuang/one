package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 检索器类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiRetrieverType implements BaseEnum<String> {

    /**
     * 纯向量检索
     */
    VECTOR("VECTOR", "向量检索"),

    /**
     * 关键词检索
     */
    BM25("BM25", "关键词检索"),

    /**
     * 混合检索
     */
    HYBRID("HYBRID", "混合检索");

    private final String value;

    private final String description;

}
