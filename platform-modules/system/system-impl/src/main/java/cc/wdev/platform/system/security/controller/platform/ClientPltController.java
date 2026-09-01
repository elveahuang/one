package cc.wdev.platform.system.security.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.security.domain.entity.ClientEntity;
import cc.wdev.platform.system.security.domain.form.ClientForm;
import cc.wdev.platform.system.security.domain.request.ClientCheckRequest;
import cc.wdev.platform.system.security.domain.request.ClientRequest;
import cc.wdev.platform.system.security.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "ClientAdminController", description = "客户端管理控制器")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "成功"),
    @ApiResponse(responseCode = "400", description = "请求参数错误")
})
public class ClientPltController extends AbstractController {

    private final ClientService clientService;

    @PreAuthorize("hasAnyAuthority('system:client')")
    @Operation(summary = "获取客户端列表")
    @ApiResponse(description = "获取客户端列表")
    @GetMapping(API_V1_SYS_PREFIX + "/client/list")
    public R<?> list(@Valid @Parameter(description = "分页查询参数") PageRequest pageRequest) {
        return R.success(clientService.findByPage(pageRequest.getPageable()));
    }

    @PreAuthorize("hasAnyAuthority('system:client')")
    @Operation(summary = "获取客户端搜索列表")
    @ApiResponse(description = "获取客户端搜索列表")
    @PostMapping(API_V1_SYS_PREFIX + "/client/search/list")
    public R<Page<ClientEntity>> list(@Valid @Parameter(description = "客户端查询参数") ClientRequest request) {
        return R.success(clientService.findClientList(request));
    }

    @PreAuthorize("hasAnyAuthority('system:client:add', 'system:client:edit')")
    @Operation(summary = "保存客户端")
    @ApiResponse(description = "保存客户端")
    @OperationLog("保存客户端")
    @PostMapping(API_V1_SYS_PREFIX + "/client/save")
    public R<?> save(@RequestBody @Parameter(description = "客户端表单参数") ClientForm from) {
        return R.success(clientService.saveClient(from));
    }

    @PreAuthorize("hasAnyAuthority('system:client:view')")
    @Operation(summary = "获取客户端详情")
    @ApiResponse(description = "获取客户端详情")
    @PostMapping(API_V1_SYS_PREFIX + "/client/details")
    public R<ClientEntity> details(@RequestParam("id") @Parameter(description = "客户端ID") Long id) {
        return R.success(clientService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('system:client:delete')")
    @Operation(summary = "删除客户端")
    @ApiResponse(description = "删除客户端")
    @OperationLog("删除客户端")
    @PostMapping(API_V1_SYS_PREFIX + "/client/delete")
    public R<?> delete(@Valid @Parameter(description = "删除客户端参数") DeleteRequest request) {
        return R.success(clientService.deleteClient(request));
    }

    @PreAuthorize("hasAnyAuthority('system:client:chcek')")
    @Operation(summary = "检查客户端是否重复")
    @ApiResponse(description = "检查客户端是否重复")
    @GetMapping(API_V1_SYS_PREFIX + "/client/check")
    public R<Boolean> check(@Valid @Parameter(description = "检查客户端重复参数") ClientCheckRequest request) {
        return R.success(clientService.checkClient(request));
    }

}
