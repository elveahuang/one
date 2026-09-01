package cc.wdev.platform.system.storage.domain.vo.webapp;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "附件文件")
public class AttachmentFileVo implements Serializable {
    /**
     * 附件文件ID
     */
    @Schema(title = "附件文件ID", description = "附件文件ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 附件链接
     */
    @Schema(title = "附件链接", description = "附件链接")
    private String url;
}
