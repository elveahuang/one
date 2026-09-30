package cc.wdev.platform.system.region.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.region.domain.entity.RegionRelationEntity;
import cc.wdev.platform.system.region.repository.RegionRelationRepository;
import cc.wdev.platform.system.region.service.RegionRelationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegionRelationServiceImpl extends BaseEntityService<RegionRelationEntity, Long, RegionRelationRepository> implements RegionRelationService {

    @Override
    public List<RegionRelationEntity> findRelations(RelationRequest request) {
        String relationType = StringUtils.nvl(request.getRelationBizType(), request.getBizType());
        if (StringUtils.isEmpty(relationType)) {
            return Collections.emptyList();
        }
        List<Long> bizIds = Lists.newArrayList();
        if (ObjectUtils.isValidId(request.getBizId())) {
            bizIds.add(request.getBizId());
        }
        if (CollectionUtils.isNotEmpty(request.getBizIdList())) {
            bizIds.addAll(request.getBizIdList());
        }
        if (CollectionUtils.isEmpty(bizIds)) {
            return Collections.emptyList();
        }
        return lambdaQueryWrapper()
            .eq(RegionRelationEntity::getBizType, relationType)
            .in(RegionRelationEntity::getBizId, bizIds)
            .list();
    }

    @Override
    public void saveRelation(RelationSaveRequest request) {
        String relationType = StringUtils.nvl(request.getRelationBizType(), request.getBizType());
        if (!ObjectUtils.isValidId(request.getBizId()) || StringUtils.isEmpty(relationType)) {
            return;
        }

        this.deleteRelation(RelationRequest.builder()
            .relationBizType(relationType)
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .build()
        );

        if (ObjectUtils.isEmpty(request.getIds())) {
            request.setIds(new Long[]{});
        }

        List<RegionRelationEntity> entityList = Arrays.stream(request.getIds()).map((id) -> RegionRelationEntity.builder()
            .bizType(relationType)
            .bizId(request.getBizId())
            .regionId(id)
            .build()
        ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(entityList)) {
            saveBatch(entityList);
            log.info("RegionRelation save success.");
        }
    }

    @Override
    public void deleteRelation(RelationRequest request) {
        List<RegionRelationEntity> entities = this.findRelations(request);
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        this.deleteBatch(entities);
    }
}
