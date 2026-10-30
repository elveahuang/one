package cc.wdev.platform.system.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户身份信息
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户身份信息")
public class UserRoleTypeVo implements Serializable {

    /**
     * 当前生效的角色类型；为空表示未切换，按全部角色合并
     */
    @Schema(description = "当前角色类型")
    private String current;

    /**
     * 用户在该租户下拥有的全部角色类型
     */
    @Schema(description = "拥有的角色类型")
    private List<String> owned;

    /**
     * 可切换的目标（排除 MEMBER，且需要至少两个才有切换意义）
     */
    @Schema(description = "可切换的角色类型")
    private List<String> available;

    /**
     * 是否显示切换入口：可用类型数量大于 1
     */
    @Schema(description = "是否可切换身份")
    private Boolean switchable;

}
