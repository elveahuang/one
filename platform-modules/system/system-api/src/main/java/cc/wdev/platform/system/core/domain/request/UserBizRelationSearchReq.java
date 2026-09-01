package cc.wdev.platform.system.core.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户业务关系查询请求")
public class UserBizRelationSearchReq extends Request {

    @NotNull
    @Schema(description = "用户ID")
    private Long userId;

    @NotEmpty
    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务ID列表")
    private List<Long> bizIds;

    @NotEmpty
    @Schema(description = "关系类型")
    private String relationType;
}
