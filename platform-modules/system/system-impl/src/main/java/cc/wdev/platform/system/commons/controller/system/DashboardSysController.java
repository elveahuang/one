package cc.wdev.platform.system.commons.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.chart.ChartVo;
import cc.wdev.platform.system.core.service.LoginSessionService;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "DashboardAdminController", description = "管理端仪表盘控制器")
public class DashboardSysController extends AbstractController {

    private final LoginSessionService loginSessionService;

    @PreAuthorize("hasAnyAuthority('system:workbench')")
    @OperationLog("登录终端来源折线图")
    @Operation(summary = "登录终端来源折线图")
    @ApiResponse(description = "登录终端来源折线图")
    @GetMapping(API_V1_SYS_PREFIX + "/login-platform-line-chart")
    public R<ChartVo> loginPlatformLineChart(
        @Parameter(description = "图表类型") @RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
        @Parameter(description = "日期") @RequestParam(value = "date", required = false, defaultValue = "") String date,
        @Parameter(description = "是否 Heavy 模式") @RequestParam(value = "goHeavy", required = false, defaultValue = "false") boolean goHeavy
    ) {
        return R.success(loginSessionService.getPlatformLineChart(type, date, goHeavy));
    }

    @PreAuthorize("hasAnyAuthority('system:workbench')")
    @OperationLog("大屏展示，系统信息")
    @Operation(summary = "大屏展示，系统信息")
    @ApiResponse(description = "大屏展示，系统信息")
    @GetMapping(API_V1_SYS_PREFIX + "/get-system-info")
    public R<?> getUserCount() {
        Map<String, Object> data = Maps.newHashMap();
        data.put("registerCount", 0);
        data.put("onlineCount", loginSessionService.getOnlineUserCount());
        return R.success(data);
    }

}
