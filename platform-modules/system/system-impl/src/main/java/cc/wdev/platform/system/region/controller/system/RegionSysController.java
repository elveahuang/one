package cc.wdev.platform.system.region.controller.system;

import cc.wdev.platform.commons.annotations.Authenticated;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.region.api.RegionApi;
import cc.wdev.platform.system.region.domain.request.RegionFilterRequest;
import cc.wdev.platform.system.region.domain.request.RegionLocateRequest;
import cc.wdev.platform.system.region.domain.vo.CityGroupVo;
import cc.wdev.platform.system.region.domain.vo.RegionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * 地区控制器
 *
 * @author erden
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "RegionController", description = "地区控制器")
public class RegionSysController {

    private final RegionApi regionApi;

    @Authenticated
    @Operation(summary = "获取地区列表")
    @GetMapping(API_V1_PREFIX + "/region/list")
    public R<List<RegionVo>> list(@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        return R.success(regionApi.listByParentId(parentId));
    }

    @Authenticated
    @Operation(summary = "获取城市分组列表")
    @GetMapping(API_V1_PREFIX + "/region/city-groups")
    public R<List<CityGroupVo>> cityGroups() {
        return R.success(regionApi.cityGroups());
    }

    @Authenticated
    @Operation(summary = "搜索城市")
    @GetMapping(API_V1_PREFIX + "/region/city-filter")
    public R<List<RegionVo>> filter(@Valid RegionFilterRequest request) {
        return R.success(regionApi.filterCities(request));
    }

    @Authenticated
    @Operation(summary = "位置定位")
    @PostMapping(API_V1_PREFIX + "/region/locate")
    public R<RegionVo> locate(@RequestBody @Valid RegionLocateRequest request) {
        return R.success(regionApi.locate(request));
    }
}
