package cc.wdev.platform.commons.ai.config;

import cc.wdev.platform.commons.ai.enums.AiSplittingStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplittingConfig implements Serializable {

    /**
     * 分片策略
     */
    @Builder.Default
    private String strategy = AiSplittingStrategy.TOKEN.getValue();

    /**
     * 分块大小（token）
     */
    @Builder.Default
    private int chunkSize = 500;

    /**
     * 分块重叠（token）
     */
    @Builder.Default
    private int chunkOverlap = 100;

}
