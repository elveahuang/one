package cc.wdev.platform.system.storage.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import cc.wdev.platform.system.storage.domain.biz.Config;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.Collection;

import static cc.wdev.platform.system.commons.constants.SystemAttachmentConstants.DEFAULT_CONFIG;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AttachmentRelationRequest extends Request {
    /**
     * 租户ID
     */
    @Schema(title = "租户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 附件业务类型
     */
    @Schema(title = "附件业务类型")
    private String bizType;
    /**
     * 附件关联类型
     */
    @Schema(title = "附件关联类型")
    private String relationBizType;
    /**
     * 业务类型配置
     */
    @Builder.Default
    @Schema(title = "业务类型配置", description = "业务类型配置")
    private Config config = DEFAULT_CONFIG;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Collection<Long> bizIdList;
    /**
     * 附件额外信息
     */
    @Schema(title = "附件额外信息")
    private String extra;
    /**
     * 附件文件ID集合
     */
    private Collection<Long> attachmentIdList;
}
