package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.core.domain.converter.UserBizRelationConverter;
import cc.wdev.platform.system.core.domain.entity.UserBizRelationEntity;
import cc.wdev.platform.system.core.domain.request.UserBizRelationReq;
import cc.wdev.platform.system.core.domain.request.UserBizRelationSearchReq;
import cc.wdev.platform.system.core.repository.UserBizRelationRepository;
import cc.wdev.platform.system.core.service.UserBizRelationService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class UserBizRelationServiceImpl extends BaseEntityService<UserBizRelationEntity, Long, UserBizRelationRepository> implements UserBizRelationService {

    @Override
    public void saveRelation(UserBizRelationReq saveReq) {
        if (!ObjectUtils.isValidId(saveReq.getUserId())) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        List<UserBizRelationEntity> entities = this.lambdaQueryWrapper()
            .eq(UserBizRelationEntity::getUserId, saveReq.getUserId())
            .in(UserBizRelationEntity::getBizId, saveReq.getBizIds())
            .eq(UserBizRelationEntity::getBizType, saveReq.getBizType())
            .eq(UserBizRelationEntity::getRelationType, saveReq.getRelationType())
            .list();
        Set<Long> existBizIds = entities.stream().map(UserBizRelationEntity::getBizId).collect(Collectors.toSet());
        Set<Long> bizIds = Sets.difference(Sets.newHashSet(saveReq.getBizIds()), existBizIds);
        if (CollectionUtils.isEmpty(bizIds)) {
            return;
        }

        List<UserBizRelationEntity> createList = Lists.newArrayListWithCapacity(bizIds.size());
        for (Long bizId : bizIds) {
            UserBizRelationEntity entity = UserBizRelationConverter.INSTANCE.saveReq2Entity(saveReq);
            entity.setBizId(bizId);
            entity.setDateTime(saveReq.getDateTime());
            createList.add(entity);
        }

        this.saveBatch(createList);
    }

    @Override
    public void deleteRelation(UserBizRelationReq req) {
        this.lambdaUpdateWrapper()
            .eq(ObjectUtils.isValidId(req.getUserId()), UserBizRelationEntity::getUserId, req.getUserId())
            .in(UserBizRelationEntity::getBizId, req.getBizIds())
            .eq(UserBizRelationEntity::getBizType, req.getBizType())
            .eq(UserBizRelationEntity::getRelationType, req.getRelationType())
            .remove();
    }

    @Override
    public UserBizRelationEntity getRelation(UserBizRelationReq req) {
        return this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(ObjectUtils.isValidId(req.getUserId()), UserBizRelationEntity::getUserId, req.getUserId())
            .in(UserBizRelationEntity::getBizId, req.getBizIds())
            .eq(UserBizRelationEntity::getBizType, req.getBizType())
            .eq(UserBizRelationEntity::getRelationType, req.getRelationType()));
    }

    @Override
    public Map<Long, Integer> batchHasRelation(UserBizRelationReq req) {
        if (!ObjectUtils.isValidId(req.getUserId())) {
            return Collections.emptyMap();
        }
        List<UserBizRelationEntity> entities = this.lambdaQueryWrapper()
            .eq(UserBizRelationEntity::getUserId, req.getUserId())
            .in(UserBizRelationEntity::getBizId, req.getBizIds())
            .eq(UserBizRelationEntity::getBizType, req.getBizType())
            .eq(UserBizRelationEntity::getRelationType, req.getRelationType())
            .list();
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> map = Maps.newHashMapWithExpectedSize(entities.size());
        for (UserBizRelationEntity entity : entities) {
            map.put(entity.getBizId(), BooleanTypeEnum.getTrueValue());
        }
        return map;
    }

    @Override
    public Map<Long, LocalDateTime> batchDateTime(UserBizRelationReq req) {
        if (!ObjectUtils.isValidId(req.getUserId())) {
            return Collections.emptyMap();
        }

        List<UserBizRelationEntity> entities = this.lambdaQueryWrapper()
            .eq(UserBizRelationEntity::getUserId, req.getUserId())
            .in(UserBizRelationEntity::getBizId, req.getBizIds())
            .eq(UserBizRelationEntity::getBizType, req.getBizType())
            .eq(UserBizRelationEntity::getRelationType, req.getRelationType())
            .list();
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }
        Map<Long, LocalDateTime> map = Maps.newHashMapWithExpectedSize(entities.size());
        for (UserBizRelationEntity entity : entities) {
            map.put(entity.getBizId(), entity.getDateTime());
        }
        return map;
    }

    @Override
    public Collection<Long> getBizIds(UserBizRelationSearchReq req) {
        List<UserBizRelationEntity> entities = this.lambdaQueryWrapper()
            .select(UserBizRelationEntity::getBizId)
            .eq(ObjectUtils.isValidId(req.getUserId()), UserBizRelationEntity::getUserId, req.getUserId())
            .in(CollectionUtils.isNotEmpty(req.getBizIds()), UserBizRelationEntity::getBizId, req.getBizIds())
            .eq(UserBizRelationEntity::getBizType, req.getBizType())
            .eq(UserBizRelationEntity::getRelationType, req.getRelationType())
            .orderByDesc(UserBizRelationEntity::getCreatedAt)
            .list();
        return entities.stream().map(UserBizRelationEntity::getBizId).collect(Collectors.toSet());
    }
}
