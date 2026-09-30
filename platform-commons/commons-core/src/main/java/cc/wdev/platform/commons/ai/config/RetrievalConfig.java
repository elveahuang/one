package cc.wdev.platform.commons.ai.config;

import cc.wdev.platform.commons.ai.enums.AiRetrieverType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrievalConfig implements Serializable {

    /**
     * 检索器类型
     */
    @Builder.Default
    private String retrieverType = AiRetrieverType.VECTOR.getValue();

    /**
     * 检索返回条数
     */
    @Builder.Default
    private int topK = 5;

    /**
     * 相似度阈值
     */
    @Builder.Default
    private double similarityThreshold = 0.0;

    /**
     * 是否启用重排
     */
    @Builder.Default
    private boolean rerankEnabled = true;

    /**
     * 重排返回条数
     */
    @Builder.Default
    private int rerankTopN = 0;

}
