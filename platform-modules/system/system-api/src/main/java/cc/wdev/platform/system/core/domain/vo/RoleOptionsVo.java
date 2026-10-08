package cc.wdev.platform.system.core.domain.vo;

import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class RoleOptionsVo implements Serializable {
    @Schema(title = "角色类型")
    private List<SimpleOptionVo> roleBizTypes;
    @Schema(title = "角色数据范围类型")
    private List<SimpleOptionVo> roleDataScopeTypes;
    @Schema(title = "角色分组类型")
    private List<SimpleOptionVo> roleGroupTypes;
}
