package cc.wdev.platform.system.site.controller.webapp;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.site.domain.converter.AnnouncementConverter;
import cc.wdev.platform.system.site.domain.entity.AnnouncementEntity;
import cc.wdev.platform.system.site.domain.request.AnnouncementSearchRequest;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
import cc.wdev.platform.system.site.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_WEB_PREFIX;

/**
 * @author elvea
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "AnnouncementAppController", description = "公告资讯控制器")
public class AnnouncementWebController extends AbstractController {

    private final AnnouncementService announcementService;

    @Anonymous
    @Operation(summary = "获取公告资讯列表")
    @ApiResponse(description = "获取公告资讯列表")
    @PostMapping(API_V1_WEB_PREFIX + "/announcement/list")
    public R<Page<AnnouncementVo>> list(@Parameter(description = "公告资讯查询参数") AnnouncementSearchRequest request) {
        return R.success(announcementService.findAnnouncementList(request));
    }

    @Anonymous
    @Operation(summary = "获取公告资讯详情")
    @ApiResponse(description = "获取公告资讯详情")
    @PostMapping(API_V1_WEB_PREFIX + "/announcement/details")
    public R<AnnouncementVo> details(@Parameter(description = "公告资讯ID") @RequestParam("id") Long id) {
        AnnouncementEntity entity = announcementService.findById(id);
        return R.success(AnnouncementConverter.INSTANCE.entity2Vo(entity));
    }

}
