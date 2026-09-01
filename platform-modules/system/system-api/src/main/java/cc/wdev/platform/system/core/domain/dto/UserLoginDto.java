package cc.wdev.platform.system.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "用户登录信息Dto")
public class UserLoginDto implements Serializable {
    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;
    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    /**
     * 用户名
     */
    @Schema(description = "显示名")
    private String displayName;
    /**
     * 用户名
     */
    @Schema(description = "姓名")
    private String name;
    /**
     * 手机国家区号
     */
    @Schema(description = "手机国家区号")
    private String mobileCountryCode;
    /**
     * 手机
     */
    @Schema(description = "手机")
    private String mobileNumber;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 用户状态
     */
    @Schema(description = "用户状态")
    private String status;
    /**
     * 用户所属组织
     */
    @Schema(description = "用户所属组织")
    private List<OrganizationDto> organizations;
    /**
     * 用户所属岗位
     */
    @Schema(description = "用户所属岗位")
    private List<PositionDto> positions;
    /**
     * 用户所属角色
     */
    @Schema(description = "用户所属角色")
    private List<RoleDto> roles;
    /**
     * 用户所拥有的权限
     */
    @Schema(description = "用户所拥有的权限")
    private List<AuthorityDto> authorities;
    /**
     * 用户所拥有的权限
     */
    @Schema(description = "权限合集（Spring Security）")
    private Set<GrantedAuthority> grantedAuthorities;
    /**
     * 用户所拥有的vip
     */
    @Schema(description = "用户所拥有的会员")
    private List<String> vips;
}
