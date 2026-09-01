package cc.wdev.platform.system.core.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询请求")
public class UserSearchRequest extends PageRequest {
    /**
     * 角色类型
     */
    private List<String> roleTypes;

    /**
     * 角色ID
     */
    private List<Long> roleIds;

    /**
     * 是否包含非活动状态
     */
    @Builder.Default
    private Boolean includeInActive = Boolean.FALSE;
}
