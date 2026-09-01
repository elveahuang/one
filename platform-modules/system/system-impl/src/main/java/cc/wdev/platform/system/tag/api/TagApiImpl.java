package cc.wdev.platform.system.tag.api;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.SourceTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.service.TenantService;
import cc.wdev.platform.system.tag.domain.entity.TagEntity;
import cc.wdev.platform.system.tag.domain.request.*;
import cc.wdev.platform.system.tag.domain.vo.TagTypeVo;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import cc.wdev.platform.system.tag.enums.BaseTagItemTypeEnum;
import cc.wdev.platform.system.tag.service.TagRelationService;
import cc.wdev.platform.system.tag.service.TagSequenceService;
import cc.wdev.platform.system.tag.service.TagService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;


/**
 * @author elvea
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class TagApiImpl implements TagApi {

    private final TenantService tenantService;

    private final TagService tagService;

    private final TagRelationService tagRelationService;

    private final TagSequenceService tagSequenceService;

    /**
     * @see TagApi#initialize()
     */
    @Override
    public void initialize() {
        log.info("Initialize tag start.");

        // 扫描枚举
        List<BaseTagItemTypeEnum> tagItemTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseTagItemTypeEnum.class);
        if (CollectionUtils.isEmpty(tagItemTypeEnumList)) {
            log.info("Initialize tag skip. no tag item type enum.");
            return;
        }

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize tag skip. no tenant.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            try {
                log.info("Initialize tenant [{}] tag start.", tenant.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                // 待处理标签实体
                List<TagEntity> updateEntityList = Lists.newArrayList();
                List<TagEntity> insertEntityList = Lists.newArrayList();

                for (BaseTagItemTypeEnum itemEnum : tagItemTypeEnumList) {
                    TagEntity entity = this.tagService.findByBizTypeAndTitle(itemEnum.getType(), itemEnum.getTitle());
                    if (entity != null) {
                        updateEntityList.add(entity);
                    } else {
                        entity = new TagEntity();
                        insertEntityList.add(entity);
                    }
                    entity.setTitle(itemEnum.getTitle());
                    entity.setBizType(itemEnum.getType());
                    entity.setIdx(itemEnum.getIdx());
                    entity.setStatus(1);
                    entity.setScope(1);
                    entity.setSource(SourceTypeEnum.SYSTEM.getValue());
                    entity.setActive(BooleanTypeEnum.TRUE.getValue());
                }

                this.tagService.insertBatch(insertEntityList);
                this.tagService.updateBatchById(updateEntityList);

                log.info("Initialize tenant [{}] tag done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }

        log.info("Initialize tag done.");
    }

    /**
     * @see TagApi#getTag(TagRequest)
     */
    @Override
    public TagVo getTag(TagRequest request) {
        return tagService.getTag(request);
    }

    /**
     * @see TagApi#saveTag(TagSaveRequest)
     */
    @Override
    public TagVo saveTag(TagSaveRequest form) {
        return tagService.saveTag(form);
    }

    /**
     * @see TagApi#deleteTag(TagDeleteRequest)
     */
    @Override
    public void deleteTag(TagDeleteRequest request) {
        // 查询有没有关联关系，存在关联关系不允许删除
        Boolean hasRelated = tagRelationService.hasRelation(request);
        if (hasRelated) {
            throw new ServiceException(ResponseCodeEnum.ALREADY_EXISTS_DELETE_ERROR);
        }
        tagService.deleteTag(request);
    }


    /**
     * @see TagApi#getTagType(BizTypeRequest)
     */
    @Override
    public TagTypeVo getTagType(BizTypeRequest request) {
        return tagService.getTagType(request);
    }

    /**
     * @see TagApi#getRelation(RelationRequest)
     */
    @Override
    public RelationVo<TagVo> getRelation(RelationRequest request) {
        return tagService.getRelation(request);
    }

    @Override
    public Map<Long, RelationVo<TagVo>> relationMap(RelationRequest request) {
        return tagService.relationMap(request);
    }

    /**
     * @see TagApi#saveRelation(RelationSaveRequest)
     */
    @Override
    public void saveRelation(RelationSaveRequest request) {
        tagRelationService.saveRelation(request);
    }

    /**
     * @see TagApi#deleteRelation(RelationRequest)
     */
    @Override
    public void deleteRelation(RelationRequest request) {
        tagRelationService.deleteRelation(request);
    }

    /**
     * @see TagApi#search(TagSearchRequest)
     */
    @Override
    public Page<TagVo> search(TagSearchRequest request) {
        return tagService.search(request);
    }

    /**
     * @see TagApi#list(TagSearchRequest)
     */
    @Override
    public List<TagVo> list(TagSearchRequest request) {
        return tagService.list(request);
    }

    /**
     * @see TagApi#getSequence(SequenceRequest)
     */
    @Override
    public SequenceVo getSequence(SequenceRequest request) {
        return tagSequenceService.getSequence(request);
    }

    /**
     * @see TagApi#saveSequence(SequenceRequest)
     */
    @Override
    public void saveSequence(SequenceRequest request) {
        tagSequenceService.saveSequence(request);
    }

    /**
     * 检查关联关系是否存在
     *
     * @see TagApi#checkRelationExists(RelationRequest)
     */
    @Override
    public boolean checkRelationExists(RelationRequest request) {
        return tagService.checkRelationExists(request);
    }

    /**
     * 统计关联关系数量
     *
     * @see TagApi#countRelations(RelationRequest)
     */
    @Override
    public long countRelations(RelationRequest request) {
        return tagService.countRelations(request);
    }

    /**
     * 重置序列（恢复默认顺序）
     *
     * @see TagApi#resetSequence(SequenceRequest)
     */
    @Override
    public void resetSequence(SequenceRequest request) {
        tagService.resetSequence(request);
    }

    @Override
    public void sortTag(TagSortRequest request) {
        tagService.sort(request);
    }

    @Override
    public Boolean checkTitle(TagTitleCheckRequest request) {
        return tagService.checkTitle(request);
    }
}
