package cc.wdev.platform.system.region.service.impl;

import cc.wdev.platform.commons.core.exchange.HttpExchangeManager;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.RegionTypeEnum;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.core.domain.bo.EntityIndBo;
import cc.wdev.platform.system.region.client.DataVGeoApi;
import cc.wdev.platform.system.region.domain.convert.RegionConverter;
import cc.wdev.platform.system.region.domain.entity.RegionEntity;
import cc.wdev.platform.system.region.domain.request.RegionFilterRequest;
import cc.wdev.platform.system.region.domain.request.RegionLocateRequest;
import cc.wdev.platform.system.region.domain.response.GeoJsonResponse;
import cc.wdev.platform.system.region.domain.vo.RegionVo;
import cc.wdev.platform.system.region.repository.RegionRepository;
import cc.wdev.platform.system.region.service.RegionService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author erden
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegionServiceImpl extends BaseEntityService<RegionEntity, Long, RegionRepository> implements RegionService {

    private final DataVGeoApi dataVGeoApi = HttpExchangeManager.createHttpExchangeManager()
        .getHttpExchange(DataVGeoApi.class, DataVGeoApi.BASE_URL);

    @Override
    @Transactional
    public void syncMcaData() throws Exception {
        log.info("开始同步民政部行政区划数据...");

        RegionUtils.McaData data = RegionUtils.fetchMcaData();
        log.info("获取到数据 - 省份: {}, 城市: {}, 区县: {}",
            data.getProvinceList().size(),
            data.getCityList().size(),
            data.getCountyList().size());
        List<RegionEntity> regionList = Lists.newArrayList(RegionEntity.builder()
            .id(100000L)
            .type(RegionTypeEnum.COUNTRY.getValue())
            .code("100000")
            .title("中国")
            .titleFirstLetter("Z")
            .parentId(0L)
            .build());
        regionList.addAll(processData(data));

        // 查询所有已存在的id
        Set<Long> ids = regionList.stream().map(RegionEntity::getId).collect(Collectors.toSet());
        List<RegionEntity> entities = this.findByIds(ids);
        Map<Long, RegionEntity> entityMap = entities.stream().collect(Collectors.toMap(RegionEntity::getId, e -> e));

        // 分类
        List<RegionEntity> createList = Lists.newArrayList();
        List<RegionEntity> updateList = Lists.newArrayList();
        for (RegionEntity region : regionList) {
            RegionEntity entity = entityMap.get(region.getId());
            if (ObjectUtils.isValidId(entity)) {
                if (notEquals(region, entity)) {
                    updateList.add(region);
                }
                continue;
            }
            createList.add(region);
        }

        // 批量处理
        if (CollectionUtils.isNotEmpty(createList)) {
            this.insertBatch(createList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }

        // 清理旧的数据
        this.lambdaUpdateWrapper()
            .set(RegionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .set(RegionEntity::getDeletedBy, SecurityUtils.getUid())
            .notIn(RegionEntity::getId, ids)
            .update();

        log.info("民政部行政区划数据同步完成！新增: {}, 更新: {}", createList.size(), updateList.size());
    }

    private boolean notEquals(RegionEntity scRegion, RegionEntity dbRegion) {
        return !StringUtils.equals(scRegion.getTitle(), dbRegion.getTitle())
            || !Objects.equals(scRegion.getParentId(), dbRegion.getParentId())
            || !StringUtils.equals(scRegion.getCode(), dbRegion.getCode())
            || !StringUtils.equals(scRegion.getType(), dbRegion.getType())
            || !StringUtils.equals(scRegion.getTitleFirstLetter(), dbRegion.getTitleFirstLetter());
    }


    // 处理数据
    private List<RegionEntity> processData(RegionUtils.McaData data) {
        // 构建城市代码集合，用于判断区县的父级是否是直辖市
        Set<Long> cityCodeSet = data.getCityList().stream()
            .map(region -> Long.parseLong(region.getCode()))
            .collect(java.util.stream.Collectors.toSet());

        return java.util.stream.Stream.of(
                // 处理省份数据
                data.getProvinceList().stream()
                    .map(region -> buildRegionEntity(region, 100000L, RegionTypeEnum.PROVINCE.getValue())),

                // 处理城市数据
                data.getCityList().stream()
                    .map(region -> buildRegionEntity(
                        region, Long.parseLong(region.getCode().substring(0, 2) + "0000"), RegionTypeEnum.CITY.getValue())),

                // 处理区县数据
                data.getCountyList().stream()
                    .map(region -> {
                        // 先按地级市规则计算父级ID（前4位+00）
                        Long calculatedParentId = Long.parseLong(region.getCode().substring(0, 4) + "00");

                        // 如果这个ID不在城市列表中，说明父级是直辖市（前2位+0000）
                        Long parentId = cityCodeSet.contains(calculatedParentId)
                            ? calculatedParentId
                            : Long.parseLong(region.getCode().substring(0, 2) + "0000");

                        return buildRegionEntity(region, parentId, RegionTypeEnum.COUNTY.getValue());
                    })
            )
            .flatMap(stream -> stream)
            .toList();
    }

    // 构建 RegionEntity
    private RegionEntity buildRegionEntity(RegionUtils.Region region, Long parentId, String type) {
        return RegionEntity.builder()
            .id(Long.parseLong(region.getCode()))
            .parentId(parentId)
            .type(type)
            .code(region.getCode())
            .title(region.getTitle())
            .titleFirstLetter(region.getFirstLetter())
            .build();
    }

    /**
     * @see RegionService#findByParentId(Long)
     */
    @Override
    public List<RegionEntity> findByParentId(Long parentId) {
        parentId = Optional.ofNullable(parentId).orElse(0L);

        return lambdaQueryWrapper()
            .eq(RegionEntity::getParentId, parentId)
            .eq(RegionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(RegionEntity::getId)
            .list();
    }

    @Override
    public List<RegionEntity> findCitiesAll() {
        return this.lambdaQueryWrapper()
            .select(RegionEntity::getId, RegionEntity::getTitle, RegionEntity::getTitleFirstLetter)
            .in(RegionEntity::getType, List.of(RegionTypeEnum.CITY.getValue(), RegionTypeEnum.COUNTY.getValue()))
            .eq(RegionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    @Override
    public List<RegionEntity> filterCities(RegionFilterRequest request) {
        return this.lambdaQueryWrapper()
            .in(RegionEntity::getType, List.of(RegionTypeEnum.CITY.getValue(), RegionTypeEnum.COUNTY.getValue()))
            .like(RegionEntity::getTitle, request.getQ())
            .list();
    }

    @Override
    public Map<Long, Boolean> getHasChildrenBatch(Collection<Long> parentIds) {
        if (CollectionUtils.isEmpty(parentIds)) {
            return Collections.emptyMap();
        }
        List<EntityIndBo> bos = this.mapper.getHasChildrenBatch(parentIds);
        return bos.stream().collect(Collectors.toMap(EntityIndBo::getId, EntityIndBo::getInd));
    }

    @Override
    public void findChildrenByParentId(RegionVo regionVo) {
        if (regionVo == null || !ObjectUtils.isValidId(regionVo.getId())) {
            return;
        }
        List<RegionEntity> children = lambdaQueryWrapper()
            .eq(RegionEntity::getParentId, regionVo.getId())
            .eq(RegionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        regionVo.setChildren(children.stream().map(entity -> {
            RegionVo vo = RegionConverter.INSTANCE.entity2Vo(entity);
            this.findChildrenByParentId(vo);
            return vo;
        }).toList());
    }

    @Override
    public void getRegionGeoJson() throws Exception {
        // 1. 查询所有启用的省份
        List<RegionEntity> provinceList = lambdaQueryWrapper()
            .eq(RegionEntity::getType, RegionTypeEnum.PROVINCE.getValue())
            .eq(RegionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();

        // 2. 国、省、市边界
        handleRegionGeoJson("100000.json");
        handleRegionGeoJson("100000_full.json");
        handleRegionGeoJson("100000_full_city.json");

        // 3. 省级下属区县全量边界
        for (RegionEntity province : provinceList) {
            String code = province.getCode();
            handleRegionGeoJson(code + "_full_district.json");
        }

        log.info("所有边界数据同步完成！");
    }

    @Override
    public RegionEntity locate(RegionLocateRequest request) {
        return this.mapper.locate(request);
    }

    private void handleRegionGeoJson(String path) {
        try {
            GeoJsonResponse geoResponse = dataVGeoApi.getGeoJson(path);
            if (geoResponse != null && geoResponse.getFeatures() != null) {
                // 遍历提取当前省份下的每一个市/区/县
                for (GeoJsonResponse.Feature feature : geoResponse.getFeatures()) {
                    GeoJsonResponse.Properties props = feature.getProperties();
                    Map<String, Object> geometry = feature.getGeometry();
                    if (props == null || CollectionUtils.isEmpty(geometry)) {
                        continue;
                    }
                    String code = props.getAdcode();
                    String geometryJson = JacksonUtils.toJson(geometry);
                    this.mapper.updateGeometry(code, geometryJson);
                }
            }
            // 【非常重要】休眠 1 秒，防止频繁请求被阿里云直接封禁 IP
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("获取 GeoJSON 数据失败, path: {}", path, e);
        }
    }

}
