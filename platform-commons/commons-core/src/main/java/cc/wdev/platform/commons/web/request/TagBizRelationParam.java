package cc.wdev.platform.commons.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "业务关联ID数组请求")
public class TagBizRelationParam {

    @Schema(description = "业务关联类型")
    private String bizRelationType;

    @Schema(description = "业务关联ID数组")
    private List<Long> tagIds;
}
