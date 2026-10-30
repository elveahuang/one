package cc.wdev.platform.system.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户身份端信息
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户身份端信息")
public class UserIdentitySideVo implements Serializable {

    /**
     * 当前生效的端（ADMIN / MEMBER）；为空表示未切换
     */
    @Schema(description = "当前身份端")
    private String current;

    /**
     * 用户在该租户下拥有的端
     */
    @Schema(description = "拥有的身份端")
    private List<String> owned;

    /**
     * 可切换的目标端；拥有两端时两端都可选
     */
    @Schema(description = "可切换的身份端")
    private List<String> available;

    /**
     * 是否显示切换入口：拥有两端
     */
    @Schema(description = "是否可切换身份端")
    private Boolean switchable;

}
