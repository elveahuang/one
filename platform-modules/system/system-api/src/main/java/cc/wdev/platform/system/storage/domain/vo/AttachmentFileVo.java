package cc.wdev.platform.system.storage.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "附件VO")
public class AttachmentFileVo implements Serializable {
    /**
     * 附件文件ID
     */
    @Schema(title = "附件文件ID", description = "附件文件ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 附件业务类型
     */
    @Schema(title = "附件业务类型", description = "附件业务类型")
    private String bizType;
    /**
     * 文件名
     */
    @Schema(title = "文件名", description = "文件名")
    private String filename;
    /**
     * 原始文件名
     */
    @Schema(title = "原始文件名", description = "原始文件名")
    private String originalFilename;
    /**
     * 文件大小
     */
    @Schema(title = "文件大小", description = "文件大小")
    private Long size;
    /**
     * 内容类型
     */
    @Schema(title = "内容类型", description = "内容类型")
    private String contentType;
    /**
     * 附件链接
     */
    @Schema(title = "附件key", description = "附件key")
    private String key;
    /**
     * 附件链接
     */
    @Schema(title = "附件链接", description = "附件链接")
    private String url;
    /**
     * 附加信息
     */
    @Schema(title = "额外信息", description = "额外信息")
    private String extra;
}
