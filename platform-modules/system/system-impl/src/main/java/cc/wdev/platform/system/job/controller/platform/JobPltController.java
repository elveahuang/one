package cc.wdev.platform.system.job.controller.platform;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.job.api.JobApi;
import cc.wdev.platform.system.job.domain.request.JobSaveRequest;
import cc.wdev.platform.system.job.domain.request.JobSearchRequest;
import cc.wdev.platform.system.job.domain.vo.JobVO;
import cc.wdev.platform.system.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author Belly
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "JobAdminController", description = "定时任务控制器")
public class JobPltController extends AbstractController {

    private JobService jobService;

    private JobApi jobApi;

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "获取任务列表")
    @ApiResponse(description = "获取任务列表")
    @PostMapping(API_V1_SYS_PREFIX + "/job/list")
    public R<Page<JobVO>> List(@Parameter(description = "任务查询请求") @Valid JobSearchRequest jobSearchRequest) {
        return R.success(jobService.jobList(jobSearchRequest));
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "删除任务")
    @ApiResponse(description = "删除任务")
    @PostMapping(API_V1_SYS_PREFIX + "/job/delete")
    @OperationLog("删除任务")
    public R<?> delete(@Parameter(description = "任务查询请求") @Valid JobSearchRequest jobSearchRequest) {
        return jobService.delete(jobSearchRequest);
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "保存任务")
    @ApiResponse(description = "保存任务")
    @PostMapping(API_V1_SYS_PREFIX + "/job/save")
    @OperationLog("保存任务")
    public R<?> save(@Parameter(description = "任务保存请求") @Valid JobSaveRequest jobSaveRequest) {
        return jobService.save(jobSaveRequest);
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "任务详情")
    @ApiResponse(description = "任务详情")
    @PostMapping(API_V1_SYS_PREFIX + "/job/detail")
    public R<JobVO> detail(@Parameter(description = "任务id") @RequestParam("id") Long id) {
        return jobService.detail(id);
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "校验cron是否合法")
    @ApiResponse(description = "校验cron是否合法")
    @RequestMapping(API_V1_SYS_PREFIX + "/job/cron/check")
    public R<?> check(@Parameter(description = "任务查询请求") @Valid JobSearchRequest jobSearchRequest) {
        return jobService.cronCheck(jobSearchRequest.getCron());
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "执行一次任务")
    @ApiResponse(description = "执行一次任务")
    @RequestMapping(API_V1_SYS_PREFIX + "/job/run/once")
    @OperationLog("执行一次任务")
    public R<?> runOnce(@Parameter(description = "任务id") @RequestParam("id") Long id) {
        return jobService.runOnce(id);
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "切换任务状态")
    @ApiResponse(description = "切换任务状态")
    @RequestMapping(API_V1_SYS_PREFIX + "/job/toggle/status")
    @OperationLog("切换任务状态")
    public R<?> toggleStatus(@Parameter(description = "任务id") @RequestParam("id") Long id) {
        return jobService.toggleStatus(id);
    }

    @PreAuthorize("hasAnyAuthority('dev:monitor:job')")
    @Operation(summary = "刷新任务")
    @ApiResponse(description = "刷新任务")
    @GetMapping(API_V1_SYS_PREFIX + "/job/flushed/task")
    @OperationLog("刷新任务")
    public R<?> flushed() {
        jobApi.initialize();
        return R.success();
    }

}
