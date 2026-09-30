package cc.wdev.platform.system.core.controller;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.domain.request.UserSearchRequest;
import cc.wdev.platform.system.core.domain.vo.UserSimpleInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "UserSearchController", description = "用户搜索控制器")
public class UserSearchController {

    private final UserApi userApi;

    @Authenticated
    @Operation(summary = "搜索用户")
    @ApiResponse(description = "搜索用户")
    @PostMapping(API_V1_PREFIX + "/user/search")
    public R<Page<UserSimpleInfoVo>> search(@Valid @Parameter(description = "用户搜索请求体") UserSearchRequest request) {
        return R.success(userApi.search(request));
    }

}
