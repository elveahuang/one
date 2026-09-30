package cc.wdev.platform.system.region.controller.webapp;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.commons.constants.SystemMappingConstants;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.api.AddressApi;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Tag(name = "AddressAppController", description = "地址控制器")
public class AddressWebController {

    private final AddressApi addressApi;

    @Authenticated
    @Operation(summary = "获取地址列表")
    @ApiResponse(description = "获取地址列表")
    @PostMapping(SystemMappingConstants.API_V1_WEB_PREFIX + "/address/relation")
    public R<RelationVo<AddressVo>> relation(@Parameter(description = "地址关联查询请求") @RequestBody @Valid RelationRequest request) {
        return R.success(addressApi.getRelation(request));
    }
}
