package cc.wdev.platform.system.storage.domain.vo;

import cc.wdev.platform.system.storage.domain.biz.Config;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemAttachmentConstants.DEFAULT_CONFIG;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "附件类型VO")
public class AttachmentVo implements Serializable {
    /**
     * 业务类型
     */
    @Schema(title = "业务类型", description = "业务类型")
    private String bizType;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID", description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 关联业务类型
     */
    @Schema(title = "关联业务类型", description = "关联业务类型")
    private String relationBizType;
    /**
     * 业务类型配置
     */
    @Builder.Default
    @Schema(title = "业务类型配置", description = "业务类型配置")
    private Config config = DEFAULT_CONFIG;
    /**
     * 文件ID列表
     */
    @Builder.Default
    @Schema(title = "文件ID列表", description = "文件ID列表")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> ids = Lists.newArrayList();
    /**
     * 文件列表
     */
    @Builder.Default
    @Schema(title = "文件列表", description = "文件列表")
    private List<AttachmentFileVo> files = Lists.newArrayList();
}
