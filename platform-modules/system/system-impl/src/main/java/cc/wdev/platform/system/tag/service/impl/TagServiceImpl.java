package cc.wdev.platform.system.tag.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
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
import cc.wdev.platform.system.tag.domain.converter.TagConverter;
import cc.wdev.platform.system.tag.domain.entity.TagEntity;
import cc.wdev.platform.system.tag.domain.entity.TagRelationEntity;
import cc.wdev.platform.system.tag.domain.entity.TagSequenceEntity;
import cc.wdev.platform.system.tag.domain.request.*;
import cc.wdev.platform.system.tag.domain.vo.TagTypeVo;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import cc.wdev.platform.system.tag.repository.TagRepository;
import cc.wdev.platform.system.tag.service.TagRelationService;
import cc.wdev.platform.system.tag.service.TagSequenceService;
import cc.wdev.platform.system.tag.service.TagService;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;
import static cc.wdev.platform.commons.utils.StringUtils.nvl;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.TAG;

/**
 * @author irving
 */
@Slf4j
@Service
@AllArgsConstructor
public class TagServiceImpl extends BaseCachingEntityService<TagEntity, Long, TagRepository> implements TagService {

    private final BizTypeApi bizTypeApi;

    private final TagRelationService tagRelationService;

    private final TagSequenceService tagSequenceService;

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(TAG).build();

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see TagService#getTagType(BizTypeRequest)
     */
    @Override
    public TagTypeVo getTagType(BizTypeRequest request) {
        String bizTypeCode = nvl(request.getType(), "").trim();
        BizTypeVo<?> bizType = bizTypeApi.getBizType(CoreBizGroupTypeEnum.TAG_TYPE.getValue(), bizTypeCode);
        TagTypeVo vo = TagTypeVo.builder().code(bizType.getBizType()).build();
        if (vo != null && request.isWithItem()) {
            List<TagEntity> entityList = lambdaQueryWrapper()
                .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .eq(TagEntity::getBizType, bizType.getBizType())
                .list();
            if (CollectionUtils.isNotEmpty(entityList)) {
                vo.setItems(entityList.stream().map(TagConverter.INSTANCE::entity2Vo).toList());
            }
        }
        return vo;
    }

    /**
     * @see TagService#getRelation(RelationRequest)
     */
    @Override
    public RelationVo<TagVo> getRelation(RelationRequest request) {
        List<TagRelationEntity> relationList = this.tagRelationService.findRelations(request);

        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }

        RelationVo<TagVo> vo = RelationVo.<TagVo>builder()
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .relationBizType(relationBizType)
            .build();

        List<TagEntity> tagList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(relationList)) {
            tagList.addAll(this.findByIds(relationList.stream().map(TagRelationEntity::getTagId).toList()));
        }
        if (CollectionUtils.isNotEmpty(tagList)) {
            vo.setIds(tagList.stream().map(TagEntity::getId).toArray(Long[]::new));
            vo.setItems(tagList.stream().map(TagConverter.INSTANCE::entity2Vo).toList());
        }
        return vo;
    }

    /**
     * @see TagService#search(TagSearchRequest)
     */
    @Override
    public Page<TagVo> search(TagSearchRequest request) {
        String bizType = nvl(request.getBizType()).trim();
        request.setBizType(bizType);
        Page<TagEntity> page = this.findByPage(request);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return Page.empty(request.getPageable());
        }
        List<TagVo> list = page.getContent().stream().map(TagConverter.INSTANCE::entity2Vo).toList();
        return toSpringDataPage(request.getPageable(), list, page.getTotalElements());
    }

    /**
     * @see TagService#list(TagSearchRequest)
     */
    @Override
    public List<TagVo> list(TagSearchRequest request) {
        List<TagEntity> entities = this.mapper.list(request);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream().map(TagConverter.INSTANCE::entity2Vo).toList();
    }

    /**
     * @see TagService#findByPage(TagSearchRequest)
     */
    @Override
    public Page<TagEntity> findByPage(TagSearchRequest request) {
        IPage<TagEntity> page = lambdaQueryWrapper()
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(TagEntity::getBizType, request.getBizType())
            .in(!ObjectUtils.isEmpty(request.getTenantId()), TagEntity::getTenantId, List.of(request.getTenantId(), 0L))
            .in(CollectionUtils.isNotEmpty(request.getBizIdList()), TagEntity::getBizId, request.getBizIdList())
            .and(StringUtils.isNotEmpty(request.getQ()), w -> w
                .like(TagEntity::getTitle, request.getQ())
                .or()
                .like(TagEntity::getDescription, request.getQ())
            )
            .orderByAsc(TagEntity::getIdx)
            .orderByDesc(TagEntity::getCreatedAt)
            .page(getMyBatisPlusPage(request.getPageable()));
        return toSpringDataPage(page);
    }

    /**
     * @see TagService#saveTag(TagSaveRequest)
     */
    @Override
    public TagVo saveTag(TagSaveRequest form) {
        TagEntity entity;
        form.setTitle(nvl(form.getTitle()));
        form.setDescription(nvl(form.getDescription()));

        if (ObjectUtils.isValidId(form.getId())) {
            // 更新时，必须同时校验 ID, TenantId, BizId, 防止越权
            entity = this.findOneByWrapper(lambdaQueryWrapper()
                .eq(TagEntity::getId, form.getId())
                .eq(ObjectUtils.isValidId(form.getTenantId()), TagEntity::getTenantId, form.getTenantId())
                .in(CollectionUtils.isNotEmpty(form.getBizIdList()), TagEntity::getBizId, form.getBizIdList()));
            if (entity == null) {
                throw new ServiceException(ResponseCodeEnum.NOT_PRESENT);
            }
            TagConverter.INSTANCE.updateEntityFromForm(form, entity);
        } else {
            entity = TagConverter.INSTANCE.formToEntity(form);
        }
        if (StringUtils.isBlank(form.getTitle())) {
            entity.setTitle(generateCode("TAG"));
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);

        return TagConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see TagService#deleteTag(TagDeleteRequest)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(TagDeleteRequest request) {
        List<TagEntity> entities = this.lambdaQueryWrapper()
            .eq(TagEntity::getBizType, request.getBizType())
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(!ObjectUtils.isEmpty(request.getTenantId()), TagEntity::getTenantId, request.getTenantId())
            .eq(ObjectUtils.isValidId(request.getBizId()), TagEntity::getBizId, request.getBizId())
            .in(TagEntity::getId, request.getIds())
            // 不允许删除系统默认标签
            .ne(TagEntity::getSource, SourceTypeEnum.SYSTEM.getValue())
            .list();
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        for (TagEntity e : entities) {
            deleteById(e.getId());
            tagSequenceService.deleteByTagId(e.getId());
        }
    }

    /**
     * @see EntityService#findById(Serializable)
     */
    @Override
    public TagEntity findById(Long id) {
        return this.findByCacheKey(cacheKeyGenerator.byId(id), key -> super.findById(id));
    }

    /**
     * @see TagService#findByTitle(String)
     */
    @Override
    public TagEntity findByTitle(String title) {
        return this.findByCacheKey(cacheKeyGenerator.byCode(title), key -> lambdaQueryWrapper()
            .eq(TagEntity::getTitle, title)
            .one()
        );
    }

    /**
     * @see TagService#findByBizTypeAndTitle(String, String)
     */
    @Override
    public TagEntity findByBizTypeAndTitle(String bizType, String title) {
        return this.findOneByWrapper(lambdaQueryWrapper()
            .eq(TagEntity::getBizType, bizType)
            .eq(TagEntity::getTitle, title)
        );
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void deleteById(Long id) {
        TagEntity entity = findById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return;
        }

        this.softDelete(entity);

        // 删除关联关系
        tagRelationService.deleteRelation(entity.getId());
    }

    @Override
    public void deleteByTitle(String title) {
        TagEntity entity = findByTitle(title);
        if (ObjectUtils.isEmpty(entity)) {
            return;
        }

        this.softDelete(entity);

        // 删除关联关系
        tagRelationService.deleteRelation(entity.getId());
    }

    /**
     * @see TagService#updateByTitle(TagSaveRequest)
     */
    @Override
    public void updateByTitle(TagSaveRequest form) {
        TagEntity entity = TagConverter.INSTANCE.formToEntity(form);
        lambdaUpdateWrapper()
            .eq(TagEntity::getTitle, form.getTitle())
            .update(entity);
        setCache(entity);
    }

    /**
     * @see TagService#checkTitle(Long, String)
     */
    @Override
    public Boolean checkTitle(Long id, String title) {
        return !lambdaQueryWrapper()
            .ne(existsById(id), TagEntity::getId, id)
            .eq(TagEntity::getTitle, title)
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

    @Override
    public Boolean checkTitle(TagTitleCheckRequest request) {
        return !lambdaQueryWrapper()
            .ne(existsById(request.getId()), TagEntity::getId, request.getId())
            .eq(!ObjectUtils.isEmpty(request.getTid()), TagEntity::getTenantId, request.getTid())
            .eq(TagEntity::getBizType, request.getBizType())
            .eq(TagEntity::getTitle, request.getTitle())
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

    /**
     * @see TagService#deleteByType(String)
     */
    @Override
    public void deleteByType(String bizType) {
        lambdaQueryWrapper()
            .eq(TagEntity::getBizType, bizType)
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list().forEach(item -> deleteById(item.getId()));
    }

    /**
     * @see TagService#checkRelationExists(RelationRequest)
     */
    @Override
    public Boolean checkRelationExists(RelationRequest request) {
        List<TagRelationEntity> relations = tagRelationService.findRelations(request);
        return !relations.isEmpty();
    }

    /**
     * @see TagService#countRelations(RelationRequest)
     */
    @Override
    public Integer countRelations(RelationRequest request) {
        List<TagRelationEntity> relations = tagRelationService.findRelations(request);
        return relations.size();
    }

    /**
     * @see TagService#resetSequence(SequenceRequest)
     */
    @Override
    public void resetSequence(SequenceRequest request) {
        // 删除现有序列，恢复默认顺序
        List<TagSequenceEntity> sequences = tagSequenceService.findSequence(request);
        for (TagSequenceEntity sequence : sequences) {
            tagSequenceService.deleteById(sequence.getId());
        }
    }

    /**
     * @see TagService#getTag(TagRequest)
     */
    @Override
    public TagVo getTag(TagRequest request) {
        TagEntity entity = this.findOneByWrapper(lambdaQueryWrapper()
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(ObjectUtils.isValidId(request.getTenantId()), TagEntity::getTenantId, request.getTenantId())
            .in(CollectionUtils.isNotEmpty(request.getBizIdList()), TagEntity::getBizId, request.getBizIdList())
            .eq(TagEntity::getId, request.getTagId()));
        return TagConverter.INSTANCE.entity2Vo(entity);
    }

    @Override
    public Map<Long, RelationVo<TagVo>> relationMap(RelationRequest request) {
        List<TagRelationEntity> relationList = this.tagRelationService.findRelations(request);
        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }
        Map<Long, List<TagRelationEntity>> relationMap = Maps.newHashMap();
        List<Long> tagIds = Lists.newArrayListWithCapacity(relationList.size());
        for (TagRelationEntity relation : relationList) {
            Long bizId = relation.getBizId();
            Long tagId = relation.getTagId();
            tagIds.add(tagId);
            relationMap.putIfAbsent(bizId, Lists.newArrayList());
            relationMap.get(bizId).add(relation);
        }

        List<TagEntity> entityList = this.findByIds(tagIds);
        Map<Long, TagEntity> entityMap = entityList.stream().collect(Collectors.toMap(TagEntity::getId, e -> e));

        Map<Long, RelationVo<TagVo>> map = Maps.newHashMapWithExpectedSize(relationMap.size());
        for (Long bizId : relationMap.keySet()) {
            List<TagRelationEntity> relations = relationMap.get(bizId);
            RelationVo<TagVo> vo = RelationVo.<TagVo>builder()
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .relationBizType(relationBizType)
                .build();
            if (CollectionUtils.isEmpty(relations)) {
                map.put(bizId, vo);
                continue;
            }
            List<Long> ids = Lists.newArrayListWithCapacity(relations.size());
            List<TagEntity> items = Lists.newArrayListWithCapacity(relations.size());
            for (TagRelationEntity relation : relations) {
                Long tagId = relation.getTagId();
                ids.add(tagId);
                items.add(entityMap.get(tagId));
            }
            vo.setIds(ids.toArray(Long[]::new));
            vo.setItems(items.stream().map(TagConverter.INSTANCE::entity2Vo).toList());
            map.put(bizId, vo);
        }
        return map;
    }

    @Override
    public void sort(TagSortRequest request) {
        List<Long> tagIds = request.getIds();
        if (CollectionUtils.isEmpty(tagIds)
            || !ObjectUtils.isValidId(request.getBizId())
            || StringUtils.isBlank(request.getBizType())) {
            return;
        }

        List<TagEntity> entities = this.lambdaQueryWrapper()
            .select(TagEntity::getId)
            .eq(TagEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(TagEntity::getBizId, request.getBizId())
            .eq(TagEntity::getBizType, request.getBizType())
            .in(TagEntity::getId, request.getIds())
            .list();
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        List<TagEntity> updateList = Lists.newArrayListWithCapacity(tagIds.size());
        for (int i = 0; i < tagIds.size(); i++) {
            Long id = tagIds.get(i);
            if (entities.stream().noneMatch(entity -> Objects.equals(entity.getId(), id))) {
                continue;
            }
            TagEntity update = new TagEntity();
            update.setId(id);
            update.setIdx(i);
            updateList.add(update);
        }
        super.updateBatchById(updateList);
    }

    @Override
    public void setCache(TagEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (ObjectUtils.isValidId(model.getId())) {
                getCacheService().set(getCacheKeyGenerator().byId(model.getId()), model);
            }
            if (StringUtils.isNotEmpty(model.getBizType()) && StringUtils.isNotEmpty(model.getTitle())) {
                getCacheService().set(getCacheKeyGenerator().byBizType(model.getBizType(), model.getTitle()), model);
            }
        }
    }

    @Override
    public void deleteCache(TagEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(getCacheKeyGenerator().byId(model.getId()));
            }
            if (StringUtils.isNotEmpty(model.getBizType()) && StringUtils.isNotEmpty(model.getTitle())) {
                getCacheService().delete(getCacheKeyGenerator().byBizType(model.getBizType(), model.getTitle()));
            }
        }
    }

}
