package cc.wdev.platform.system.storage.domain.vo.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.compress.utils.Lists;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * @author elvea
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "附件")
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
     * 文件列表
     */
    @Builder.Default
    @Schema(title = "文件列表", description = "文件列表")
    private List<AttachmentFileVo> files = Lists.newArrayList();
}
