package cc.wdev.platform.system.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户数统计请求")
public class UserCountRequest {

    @Schema(description = "角色类型")
    private List<String> roleTypes;

}
