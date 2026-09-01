package cc.wdev.platform.system.region.api;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.domain.convert.RegionConverter;
import cc.wdev.platform.system.region.domain.entity.RegionEntity;
import cc.wdev.platform.system.region.domain.entity.RegionRelationEntity;
import cc.wdev.platform.system.region.domain.request.RegionFilterRequest;
import cc.wdev.platform.system.region.domain.request.RegionLocateRequest;
import cc.wdev.platform.system.region.domain.vo.CityGroupVo;
import cc.wdev.platform.system.region.domain.vo.RegionVo;
import cc.wdev.platform.system.region.service.RegionRelationService;
import cc.wdev.platform.system.region.service.RegionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class RegionApiImpl implements RegionApi {

    private final RegionService regionService;

    private final RegionRelationService regionRelationService;

    // ------------------------------------------------------------------------------
    // Base
    // ------------------------------------------------------------------------------

    /**
     * @see RegionApi#initialize()
     */
    @Override
    public void initialize() {
        try {
            this.regionService.syncMcaData();
            this.regionService.getRegionGeoJson();
        } catch (Exception e) {
            log.error("RegionApiImpl initialize error.", e);
        }
    }

    /**
     * @see RegionApi#listByParentId(Long)
     */
    @Override
    public List<RegionVo> listByParentId(Long parentId) {
        List<RegionEntity> entities = regionService.findByParentId(parentId);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        // 批量获取是否有子节点
        Map<Long, Boolean> hasChildernMap = regionService.getHasChildrenBatch(entities.stream().map(RegionEntity::getId).toList());
        List<RegionVo> vos = Lists.newArrayListWithCapacity(entities.size());
        for (RegionEntity entity : entities) {
            RegionVo vo = RegionConverter.INSTANCE.entity2Vo(entity);
            vo.setHasChildren(hasChildernMap.getOrDefault(vo.getId(), Boolean.FALSE));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * @see RegionApi#findById(Long)
     */
    @Override
    public RegionVo findById(Long id) {
        RegionEntity entity = regionService.findById(id);
        if (entity == null) {
            return null;
        }
        RegionVo vo = RegionConverter.INSTANCE.entity2Vo(entity);
        if (ObjectUtils.isValidId(entity.getParentId())) {
            vo.setParent(this.findById(entity.getParentId()));
        }
        return vo;
    }

    /**
     * @see RegionApi#findByIds(List)
     */
    @Override
    public List<RegionVo> findByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<RegionEntity> entities = regionService.findByIds(ids);
        return entities.stream()
            .map(RegionConverter.INSTANCE::entity2Vo)
            .toList();
    }

    @Override
    public List<CityGroupVo> cityGroups() {
        List<RegionEntity> regions = regionService.findCitiesAll();
        if (CollectionUtils.isEmpty(regions)) {
            return Collections.emptyList();
        }
        Map<String, List<RegionEntity>> groupMap = regions.stream().collect(Collectors.groupingBy(RegionEntity::getTitleFirstLetter));
        List<CityGroupVo> vos = Lists.newArrayListWithCapacity(groupMap.size());
        groupMap.forEach((titleFirstLetter, list) -> {
            CityGroupVo vo = new CityGroupVo();
            vo.setTitleFirstLetter(titleFirstLetter);
            if (CollectionUtils.isEmpty(list)) {
                vo.setRegions(Collections.emptyList());
                return;
            }
            List<RegionVo> regionVos = Lists.newArrayListWithCapacity(list.size());
            for (RegionEntity entity : list) {
                regionVos.add(RegionConverter.INSTANCE.entity2Vo(entity));
            }
            vo.setRegions(regionVos);
            vos.add(vo);
        });
        return vos;
    }


    @Override
    public void saveRelation(RelationSaveRequest request) {
        regionRelationService.saveRelation(request);
    }

    @Override
    public void deleteRelation(RelationRequest request) {
        regionRelationService.deleteRelation(request);
    }

    @Override
    public RelationVo<RegionVo> getRelation(RelationRequest request) {
        List<RegionRelationEntity> relationList = regionRelationService.findRelations(request);

        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }

        RelationVo<RegionVo> vo = RelationVo.<RegionVo>builder()
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .relationBizType(relationBizType)
            .build();

        List<RegionEntity> entityList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(relationList)) {
            List<RegionEntity> regions = regionService.findByIds(relationList.stream().map(RegionRelationEntity::getRegionId).toList());
            entityList.addAll(regions);
        }
        if (CollectionUtils.isNotEmpty(entityList)) {
            List<Long> regionIds = Lists.newArrayListWithCapacity(entityList.size());
            List<RegionVo> regionVos = Lists.newArrayListWithCapacity(entityList.size());
            for (RegionEntity entity : entityList) {
                regionIds.add(entity.getId());
                RegionVo regionVo = RegionConverter.INSTANCE.entity2Vo(entity);
                if (request.getIncludeChildren()) {
                    // 获取子节点
                    regionService.findChildrenByParentId(regionVo);
                }
                regionVos.add(regionVo);
            }
            vo.setIds(regionIds.toArray(Long[]::new));
            vo.setItems(regionVos);
        }
        return vo;
    }

    @Override
    public Map<Long, RelationVo<RegionVo>> relationMap(RelationRequest request) {
        List<RegionRelationEntity> relationList = regionRelationService.findRelations(request);
        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }
        Map<Long, List<RegionRelationEntity>> relationMap = Maps.newHashMap();
        List<Long> addressIds = Lists.newArrayListWithCapacity(relationList.size());
        for (RegionRelationEntity relation : relationList) {
            Long bizId = relation.getBizId();
            Long regionId = relation.getRegionId();
            addressIds.add(regionId);
            relationMap.putIfAbsent(bizId, Lists.newArrayList());
            relationMap.get(bizId).add(relation);
        }

        List<RegionEntity> regions = regionService.findByIds(addressIds);
        Map<Long, RegionEntity> entityMap = regions.stream().collect(Collectors.toMap(RegionEntity::getId, e -> e));

        Map<Long, RelationVo<RegionVo>> map = Maps.newHashMapWithExpectedSize(relationMap.size());
        for (Long bizId : relationMap.keySet()) {
            List<RegionRelationEntity> relations = relationMap.get(bizId);
            RelationVo<RegionVo> vo = RelationVo.<RegionVo>builder()
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .relationBizType(relationBizType)
                .build();
            if (CollectionUtils.isEmpty(relations)) {
                map.put(bizId, vo);
                continue;
            }
            List<Long> ids = Lists.newArrayListWithCapacity(relations.size());
            List<RegionEntity> items = Lists.newArrayListWithCapacity(relations.size());
            for (RegionRelationEntity relation : relations) {
                Long regionId = relation.getRegionId();
                ids.add(regionId);
                items.add(entityMap.get(regionId));
            }
            vo.setIds(ids.toArray(Long[]::new));
            vo.setItems(items.stream().map(RegionConverter.INSTANCE::entity2Vo).toList());
            map.put(bizId, vo);
        }
        return map;
    }

    @Override
    public List<RegionVo> filterCities(RegionFilterRequest request) {
        List<RegionEntity> entities = regionService.filterCities(request);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<RegionVo> vos = Lists.newArrayListWithCapacity(entities.size());
        for (RegionEntity entity : entities) {
            vos.add(RegionConverter.INSTANCE.entity2Vo(entity));
        }
        return vos;
    }

    @Override
    public RegionVo locate(RegionLocateRequest request) {
        RegionEntity entity = regionService.locate(request);
        if (!ObjectUtils.isValidId(entity)) {
            return null;
        }
        RegionVo vo = RegionConverter.INSTANCE.entity2Vo(entity);
        if (ObjectUtils.isValidId(entity.getParentId())) {
            vo.setParent(this.findById(entity.getParentId()));
        }
        return vo;
    }
}
