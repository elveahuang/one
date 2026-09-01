package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiVectorizationStatus implements BaseEnum<Integer> {
    PENDING(1, "PENDING", "待向量化"),
    PROCESSING(2, "PROCESSING", "向量化进行中"),
    COMPLETED(3, "COMPLETED", "已向量化"),
    FAILED(4, "FAILED", "向量化失败");

    private final Integer value;
    private final String code;
    private final String description;
}
