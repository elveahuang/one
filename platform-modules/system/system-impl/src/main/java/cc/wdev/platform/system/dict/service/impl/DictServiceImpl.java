package cc.wdev.platform.system.dict.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.SourceTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import cc.wdev.platform.system.dict.domain.converter.DictConverter;
import cc.wdev.platform.system.dict.domain.entity.DictEntity;
import cc.wdev.platform.system.dict.domain.entity.DictRelationEntity;
import cc.wdev.platform.system.dict.domain.entity.DictSequenceEntity;
import cc.wdev.platform.system.dict.domain.request.*;
import cc.wdev.platform.system.dict.domain.vo.DictTypeVo;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import cc.wdev.platform.system.dict.repository.DictRepository;
import cc.wdev.platform.system.dict.service.DictRelationService;
import cc.wdev.platform.system.dict.service.DictSequenceService;
import cc.wdev.platform.system.dict.service.DictService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.DICT;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class DictServiceImpl extends BaseCachingEntityService<DictEntity, Long, DictRepository> implements DictService {

    private final BizTypeApi bizTypeApi;

    private final DictRelationService dictRelationService;

    private final DictSequenceService dictSequenceService;

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(DICT).build();

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see DictService#getDictType(BizTypeRequest)
     */
    @Override
    public DictTypeVo getDictType(BizTypeRequest request) {
        String bizTypeCode = StringUtils.nvl(request.getType()).trim();
        BizTypeVo<?> bizType = bizTypeApi.getBizType(CoreBizGroupTypeEnum.DICT_TYPE.getValue(), bizTypeCode);
        DictTypeVo typeVo = DictTypeVo.builder().code(bizType.getBizType()).build();
        if (typeVo != null && request.isWithItem()) {
            List<DictEntity> entityList = lambdaQueryWrapper()
                .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .eq(DictEntity::getBizType, bizType.getBizType())
                .list();
            if (CollectionUtils.isNotEmpty(entityList)) {
                typeVo.setItems(entityList.stream().map(DictConverter.INSTANCE::entity2Vo).toList());
            }
        }
        return typeVo;
    }

    /**
     * @see DictService#getRelation(RelationRequest)
     */
    @Override
    public RelationVo<DictVo> getRelation(RelationRequest request) {
        List<DictRelationEntity> relationList = this.dictRelationService.findRelations(request);

        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }

        RelationVo<DictVo> vo = RelationVo.<DictVo>builder()
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .relationBizType(relationBizType)
            .build();

        List<DictEntity> entityList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(relationList)) {
            entityList.addAll(this.findByIds(relationList.stream().map(DictRelationEntity::getDictId).toList()));
        }
        if (CollectionUtils.isNotEmpty(entityList)) {
            vo.setIds(entityList.stream().map(DictEntity::getId).toArray(Long[]::new));
            vo.setItems(entityList.stream().map(DictConverter.INSTANCE::entity2Vo).toList());
        }
        return vo;
    }

    /**
     * @see DictService#relationMap(RelationRequest)
     */
    @Override
    public Map<Long, RelationVo<DictVo>> relationMap(RelationRequest request) {
        List<DictRelationEntity> relationList = this.dictRelationService.findRelations(request);
        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }
        Map<Long, List<DictRelationEntity>> relationMap = Maps.newHashMap();
        List<Long> dictIds = Lists.newArrayListWithCapacity(relationList.size());
        for (DictRelationEntity relation : relationList) {
            Long bizId = relation.getBizId();
            Long dictId = relation.getDictId();
            dictIds.add(dictId);
            relationMap.putIfAbsent(bizId, Lists.newArrayList());
            relationMap.get(bizId).add(relation);
        }

        List<DictEntity> entityList = this.findByIds(dictIds);
        Map<Long, DictEntity> entityMap = entityList.stream().collect(Collectors.toMap(DictEntity::getId, e -> e));

        Map<Long, RelationVo<DictVo>> map = Maps.newHashMapWithExpectedSize(relationMap.size());
        for (Long bizId : relationMap.keySet()) {
            List<DictRelationEntity> relations = relationMap.get(bizId);
            RelationVo<DictVo> vo = RelationVo.<DictVo>builder()
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .relationBizType(relationBizType)
                .build();
            if (CollectionUtils.isEmpty(relations)) {
                map.put(bizId, vo);
                continue;
            }
            List<Long> ids = Lists.newArrayListWithCapacity(relations.size());
            List<DictEntity> items = Lists.newArrayListWithCapacity(relations.size());
            for (DictRelationEntity relation : relations) {
                Long dictId = relation.getDictId();
                ids.add(dictId);
                items.add(entityMap.get(dictId));
            }
            vo.setIds(ids.toArray(Long[]::new));
            vo.setItems(items.stream().map(DictConverter.INSTANCE::entity2Vo).toList());
            map.put(bizId, vo);
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDict(DictDeleteRequest request) {
        Boolean hasRelation = dictRelationService.hasRelation(request);
        if (hasRelation) {
            throw new ServiceException(ResponseCodeEnum.ALREADY_EXISTS_DELETE_ERROR);
        }
        List<DictEntity> entities = this.lambdaQueryWrapper()
            .eq(DictEntity::getBizType, request.getBizType())
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(DictEntity::getTenantId, request.getTenantId())
            .in(DictEntity::getId, request.getIds())
            // 不允许删除系统默认标签
            .ne(DictEntity::getSource, SourceTypeEnum.SYSTEM.getValue())
            .list();
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        this.deleteBatch(entities);
    }

    @Override
    public Boolean checkTitle(DictTitleCheckRequest request) {
        return !lambdaQueryWrapper()
            .ne(existsById(request.getId()), DictEntity::getId, request.getId())
            .eq(!ObjectUtils.isEmpty(request.getTid()), DictEntity::getTenantId, request.getTid())
            .eq(DictEntity::getBizType, request.getBizType())
            .eq(DictEntity::getTitle, request.getTitle())
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

    /**
     * @see DictService#search(DictSearchRequest)
     */
    @Override
    public Page<DictVo> search(DictSearchRequest request) {
        String bizType = StringUtils.nvl(request.getBizType()).trim();
        request.setBizType(bizType);
        Page<DictEntity> page = this.findByPage(request);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return Page.empty(request.getPageable());
        }
        List<DictVo> list = page.getContent().stream().map(DictConverter.INSTANCE::entity2Vo).toList();
        return toSpringDataPage(request.getPageable(), list, page.getTotalElements());
    }

    /**
     * @see DictService#list(DictSearchRequest)
     */
    @Override
    public List<DictVo> list(DictSearchRequest request) {
        String bizType = StringUtils.nvl(request.getBizType(), "").trim();
        List<DictEntity> entityList = lambdaQueryWrapper()
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(DictEntity::getBizType, bizType)
            .in(DictEntity::getTenantId, List.of(0L, TenantContext.getTenantId()))
            .list();
        if (CollectionUtils.isNotEmpty(entityList)) {
            return entityList.stream().map(DictConverter.INSTANCE::entity2Vo).toList();
        }
        return Collections.emptyList();
    }

    /**
     * @see DictService#findByPage(DictSearchRequest)
     */
    @Override
    public Page<DictEntity> findByPage(DictSearchRequest request) {
        IPage<DictEntity> page = lambdaQueryWrapper()
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(DictEntity::getBizType, request.getBizType())
            .in(!ObjectUtils.isEmpty(request.getTenantId()), DictEntity::getTenantId, List.of(request.getTenantId(), 0L))
            .and(StringUtils.isNotEmpty(request.getQ()), w -> w
                .like(DictEntity::getCode, request.getQ())
                .or().like(DictEntity::getTitle, request.getQ())
            )
            .page(getMyBatisPlusPage(request.getPageable()));
        return toSpringDataPage(page);
    }

    @Override
    public Boolean checkCode(DictCodeCheckRequest request) {
        return !lambdaQueryWrapper()
            .ne(existsById(request.getId()), DictEntity::getId, request.getId())
            .eq(!ObjectUtils.isEmpty(request.getTid()), DictEntity::getTenantId, request.getTid())
            .eq(DictEntity::getBizType, request.getBizType())
            .eq(DictEntity::getCode, request.getCode())
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

    /**
     * @see DictService#saveDict(DictSaveRequest)
     */
    @Override
    public void saveDict(DictSaveRequest request) {
        DictEntity entity;
        if (request.getId() != null && request.getId() > 0) {
            entity = this.findById(request.getId());
            ObjectUtils.copyNotNullProperties(request, entity);
        } else {
            entity = DictConverter.INSTANCE.requestToEntity(request);
            entity.setSource(SourceTypeEnum.NORMAL.getValue());
        }
        if (StringUtils.isBlank(request.getCode())) {
            entity.setCode(generateCode("DICT"));
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);
    }

    /**
     * @see EntityService#findById(Serializable)
     */
    @Override
    public DictEntity findById(Long id) {
        return this.findByCacheKey(cacheKeyGenerator.byId(id), key -> super.findById(id));
    }

    @Override
    public DictVo getDict(DictRequest request) {
        DictEntity entity = this.findOneByWrapper(lambdaQueryWrapper()
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(ObjectUtils.isValidId(request.getTenantId()), DictEntity::getTenantId, request.getTenantId())
            .eq(DictEntity::getId, request.getDictId()));
        return DictConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see DictService#findByCode(DictRequest)
     */
    @Override
    public DictEntity findByCode(DictRequest request) {
        return this.findByCacheKey(cacheKeyGenerator.byCode(request.getCode()), _ -> {
            return super.findOneByWrapper(lambdaQueryWrapper()
                .eq(DictEntity::getCode, request.getCode())
            );
        });
    }

    /**
     * @see DictService#findByBizTypeAndCode(String, String)
     */
    @Override
    public DictEntity findByBizTypeAndCode(String bizType, String code) {
        return this.findOneByWrapper(lambdaQueryWrapper()
            .eq(DictEntity::getBizType, bizType)
            .eq(DictEntity::getCode, code)
        );
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void deleteById(Long id) {
        DictEntity entity = findById(id);

        if (ObjectUtils.isEmpty(entity)) {
            return;
        }

        this.softDelete(entity);

        // 删除关联关系
        dictRelationService.deleteRelation(entity.getId());
    }

    @Override
    public void deleteByCode(DictRequest request) {
        DictEntity entity = findByCode(request);
        if (ObjectUtils.isEmpty(entity)) {
            return;
        }

        this.softDelete(entity);

        // 删除关联关系
        dictRelationService.deleteRelation(request.getCode());
    }


    /**
     * @see DictService#deleteByType(String)
     */
    @Override
    public void deleteByType(String bizType) {
        lambdaQueryWrapper()
            .eq(DictEntity::getBizType, bizType)
            .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list().forEach((dictItem) -> {
                deleteById(dictItem.getId());
            });
    }

    /**
     * @see DictService#checkRelationExists(RelationRequest)
     */
    @Override
    public boolean checkRelationExists(RelationRequest request) {
        List<DictRelationEntity> relations = dictRelationService.findRelations(request);
        return !relations.isEmpty();
    }

    /**
     * @see DictService#countRelations(RelationRequest)
     */
    @Override
    public long countRelations(RelationRequest request) {
        List<DictRelationEntity> relations = dictRelationService.findRelations(request);
        return relations.size();
    }

    /**
     * @see DictService#resetSequence(SequenceRequest)
     */
    @Override
    public void resetSequence(SequenceRequest request) {
        List<DictSequenceEntity> sequences = dictSequenceService.findSequence(request);
        for (DictSequenceEntity sequence : sequences) {
            dictSequenceService.deleteById(sequence.getId());
        }
    }

    /**
     * @see DictService#findDictsWithSequence(SequenceRequest)
     */
    @Override
    public List<DictVo> findDictsWithSequence(SequenceRequest request) {
        List<DictSequenceEntity> sequences = dictSequenceService.findSequence(request);

        if (sequences.isEmpty()) {
            // 没有个性化排序，使用字典原本的idx字段排序
            List<DictEntity> dictEntities = lambdaQueryWrapper()
                .eq(DictEntity::getBizType, request.getBizType())
                .eq(DictEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .orderByAsc(DictEntity::getIdx)
                .list();
            return dictEntities.stream().map(DictConverter.INSTANCE::entity2Vo).toList();
        }

        // 根据序列获取字典
        Map<Long, Integer> sequenceMap = sequences.stream()
            .collect(Collectors.toMap(
                DictSequenceEntity::getDictId,
                DictSequenceEntity::getIdx,
                (existing, replacement) -> existing
            ));

        List<Long> entityIds = sequences.stream()
            .map(DictSequenceEntity::getDictId)
            .collect(Collectors.toList());

        List<DictEntity> entities = this.findByIds(entityIds);

        return entities.stream()
            .sorted((a, b) -> {
                Integer seqA = sequenceMap.get(a.getId());
                Integer seqB = sequenceMap.get(b.getId());
                return Integer.compare(
                    seqA != null ? seqA : Integer.MAX_VALUE,
                    seqB != null ? seqB : Integer.MAX_VALUE
                );
            })
            .map(DictConverter.INSTANCE::entity2Vo)
            .collect(Collectors.toList());
    }

    @Override
    public void setCache(DictEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (ObjectUtils.isValidId(model.getId())) {
                getCacheService().set(getCacheKeyGenerator().byId(model.getId()), model);
            }
            if (StringUtils.isNotEmpty(model.getBizType()) && StringUtils.isNotEmpty(model.getCode())) {
                getCacheService().set(getCacheKeyGenerator().byBizType(model.getBizType(), model.getCode()), model);
            }
        }
    }

    @Override
    public void deleteCache(DictEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(getCacheKeyGenerator().byId(model.getId()));
            }
            if (StringUtils.isNotEmpty(model.getBizType()) && StringUtils.isNotEmpty(model.getCode())) {
                getCacheService().delete(getCacheKeyGenerator().byBizType(model.getBizType(), model.getCode()));
            }
        }
    }

}
