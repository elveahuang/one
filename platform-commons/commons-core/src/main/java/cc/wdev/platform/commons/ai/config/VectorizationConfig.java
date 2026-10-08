package cc.wdev.platform.commons.ai.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 向量化配置
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorizationConfig implements Serializable {

    /**
     * 单批向量化文档数
     */
    @Builder.Default
    private int batchSize = 20;

}
