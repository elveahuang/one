package cc.wdev.platform.system.site.controller.system;

import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.api.AnnouncementApi;
import cc.wdev.platform.system.site.domain.form.AnnouncementForm;
import cc.wdev.platform.system.site.domain.request.AnnouncementSearchRequest;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
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

import java.util.Arrays;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_SYS_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "AnnouncementAdminController", description = "公告资讯后台管理控制器")
public class AnnouncementSysController extends AbstractController {

    private final AnnouncementApi announcementApi;

    @PreAuthorize("hasAnyAuthority('system:announcement')")
    @Operation(summary = "获取公告资讯列表")
    @ApiResponse(description = "获取公告资讯列表")
    @PostMapping(API_V1_SYS_PREFIX + "/announcement/list")
    public R<Page<AnnouncementVo>> list(@Parameter(description = "公告资讯查询参数") AnnouncementSearchRequest searchRequest) {
        searchRequest.setTenantId(TenantContext.getTenantId());
        if (searchRequest.getAllowCommentInd() == null) {
            searchRequest.setAllowCommentInd(BooleanTypeEnum.TRUE.getValue());
        }
        return R.success(announcementApi.findPageAnnouncement(searchRequest));
    }

    @PreAuthorize("hasAnyAuthority('system:announcement')")
    @Operation(summary = "获取公告资讯详情")
    @ApiResponse(description = "获取公告资讯详情")
    @PostMapping(API_V1_SYS_PREFIX + "/announcement/details")
    public R<AnnouncementVo> details(@Parameter(description = "公告资讯ID") @RequestParam("id") Long id) {
        return R.success(announcementApi.getAnnouncement(id));
    }


    @PreAuthorize("hasAnyAuthority('system:announcement')")
    @Operation(summary = "保存公告资讯")
    @ApiResponse(description = "保存公告资讯")
    @PostMapping(API_V1_SYS_PREFIX + "/announcement/save")
    @OperationLog("保存公告资讯")
    public R<?> save(@Parameter(description = "公告资讯表单") @Valid AnnouncementForm form) {
        this.announcementApi.saveAnnouncement(form);
        return R.success();
    }

    @PreAuthorize("hasAnyAuthority('system:announcement')")
    @Operation(summary = "删除公告资讯")
    @ApiResponse(description = "删除公告资讯")
    @PostMapping(API_V1_SYS_PREFIX + "/announcement/delete")
    @OperationLog("删除公告资讯")
    public R<?> delete(@Parameter(description = "删除公告资讯请求参数") @Valid DeleteRequest request) {
        if (request != null && request.getIds() != null && request.getIds().length > 0) {
            announcementApi.deleteAnnouncementBatchById(Arrays.asList(request.getIds()));
        }
        return R.success();
    }

}
