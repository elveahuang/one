package cc.wdev.platform.system.core.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthorityVo implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    private String code;
    private String title;
    private String authorityType;
    private String authorityScopeType;
    private String description;
    private Integer idx;
    private Integer active;
}
