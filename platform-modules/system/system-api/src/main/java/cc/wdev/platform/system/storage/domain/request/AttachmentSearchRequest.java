package cc.wdev.platform.system.storage.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "附件查询请求")
public class AttachmentSearchRequest extends PageRequest {

    @Schema(description = "附件业务类型")
    private String bizType;

}
