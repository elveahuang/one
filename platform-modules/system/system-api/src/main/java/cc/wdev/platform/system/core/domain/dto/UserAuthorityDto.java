package cc.wdev.platform.system.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "用户权限实体")
public class UserAuthorityDto implements Serializable {

    @Builder.Default
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id = 0L;

    @Builder.Default
    @Schema(description = "角色")
    private List<RoleDto> roles = Collections.emptyList();

    @Builder.Default
    @Schema(description = "权限合集")
    private List<AuthorityDto> authorities = Collections.emptyList();

    @Builder.Default
    @Schema(description = "权限合集（Spring Security）")
    private Set<GrantedAuthority> grantedAuthorities = Collections.emptySet();

}
