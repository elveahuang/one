package cc.wdev.platform.commons.ai.domain.request;

import cc.wdev.platform.commons.ai.model.ModelConfig;
import cc.wdev.platform.commons.web.request.Request;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleImageRequest extends Request {

    /**
     * Request ID
     */
    private String requestId;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 数量
     */
    @Builder.Default
    private int n = 1;

    /**
     * 图片质量
     */
    private String quality;

    /**
     * 图片尺寸
     */
    private String size;

    /**
     * 提示词
     */
    @Builder.Default
    private boolean watermarkEnabled = true;

    /**
     * 模型配置
     */
    private ModelConfig config;

}
