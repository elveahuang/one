package cc.wdev.platform.system.region.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.system.commons.constants.SystemMappingConstants;
import cc.wdev.platform.system.region.api.AddressApi;
import cc.wdev.platform.system.region.domain.form.AddressForm;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;
import cc.wdev.platform.system.region.domain.request.AddressRequest;
import cc.wdev.platform.system.region.domain.request.AddressSearchRequest;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author elvea
 */
@AllArgsConstructor
@RestController
@Tag(name = "AddressAdminController", description = "地址后台管理控制器")
public class AddressSysController {

    private final AddressApi addressApi;

    @PreAuthorize("hasAnyAuthority('resource:address:view')")
    @Operation(summary = "获取地址列表")
    @ApiResponse(description = "获取地址列表")
    @PostMapping(SystemMappingConstants.API_V1_SYS_PREFIX + "/address/list")
    public R<Page<AddressVo>> list(@Parameter(description = "地址查询请求") AddressSearchRequest request) {
        request.setBizIdList(List.of(SecurityUtils.getUid()));
        request.setTenantId(TenantContext.getTenantId());
        return R.success(addressApi.findPageByBizType(request));
    }

    @PreAuthorize("hasAnyAuthority('resource:address:view')")
    @Operation(summary = "获取地址详情")
    @ApiResponse(description = "获取地址详情")
    @PostMapping(SystemMappingConstants.API_V1_SYS_PREFIX + "/address/details")
    public R<AddressVo> details(@Parameter(description = "地址ID") @RequestParam("id") Long id,
                                @Parameter(description = "业务类型") @RequestParam("bizType") String bizType) {
        AddressRequest request = new AddressRequest();
        request.setTenantId(TenantContext.getTenantId());
        request.setBizIdList(List.of(SecurityUtils.getUid()));
        request.setAddressId(id);
        request.setBizType(bizType);
        return R.success(addressApi.getAddress(request));
    }

    @PreAuthorize("hasAnyAuthority('resource:address:add','resource:address:edit')")
    @OperationLog("保存地址")
    @Operation(summary = "保存地址")
    @ApiResponse(description = "保存地址")
    @PostMapping(SystemMappingConstants.API_V1_SYS_PREFIX + "/address/save")
    public R<?> save(@Parameter(description = "地址保存表单") @Valid AddressForm form) throws Exception {
        addressApi.saveAddress(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('resource:address:delete')")
    @OperationLog("删除地址")
    @Operation(summary = "删除地址")
    @ApiResponse(description = "删除地址")
    @PostMapping(SystemMappingConstants.API_V1_SYS_PREFIX + "/address/delete")
    public R<?> delete(@Parameter(description = "地址删除请求") @Valid AddressDeleteRequest request) {
        request.setTenantId(TenantContext.getTenantId());
        request.setBizId(SecurityUtils.getUid());
        addressApi.deleteAddress(request);
        return R.success();
    }
}
