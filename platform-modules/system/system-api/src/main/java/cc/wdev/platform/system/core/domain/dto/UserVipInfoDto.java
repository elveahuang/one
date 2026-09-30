package cc.wdev.platform.system.core.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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

@Schema(description = "用户会员信息")
public class UserVipInfoDto implements Serializable {
    @Schema(description = "会员列表")
    private List<?> vips;
}
