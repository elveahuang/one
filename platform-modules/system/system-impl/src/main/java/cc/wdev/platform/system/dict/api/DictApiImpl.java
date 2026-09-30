package cc.wdev.platform.system.dict.api;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.SourceTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.service.TenantService;
import cc.wdev.platform.system.dict.domain.converter.DictConverter;
import cc.wdev.platform.system.dict.domain.entity.DictEntity;
import cc.wdev.platform.system.dict.domain.request.DictRequest;
import cc.wdev.platform.system.dict.domain.request.DictSaveRequest;
import cc.wdev.platform.system.dict.domain.request.DictSearchRequest;
import cc.wdev.platform.system.dict.domain.vo.DictTypeVo;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import cc.wdev.platform.system.dict.enums.BaseDictItemTypeEnum;
import cc.wdev.platform.system.dict.service.DictRelationService;
import cc.wdev.platform.system.dict.service.DictSequenceService;
import cc.wdev.platform.system.dict.service.DictService;
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
public class DictApiImpl implements DictApi {

    private final TenantService tenantService;

    private final DictService dictService;

    private final DictRelationService dictRelationService;

    private final DictSequenceService dictSequenceService;

    /**
     * @see DictApi#initialize()
     */
    @Override
    public void initialize() {
        log.info("Initialize dict start.");

        // 扫描枚举
        List<BaseDictItemTypeEnum> dictItemTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseDictItemTypeEnum.class);
        if (CollectionUtils.isEmpty(dictItemTypeEnumList)) {
            log.info("Initialize dict skip. no dict item type enum.");
            return;
        }

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize dict skip. no tenant.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            try {
                log.info("Initialize tenant [{}] dict start.", tenant.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                // 待处理字典实体
                List<DictEntity> updateEntityList = Lists.newArrayList();
                List<DictEntity> insertEntityList = Lists.newArrayList();

                for (BaseDictItemTypeEnum itemEnum : dictItemTypeEnumList) {
                    DictEntity entity = this.dictService.findByBizTypeAndCode(itemEnum.getType(), itemEnum.getCode());
                    if (entity != null) {
                        updateEntityList.add(entity);
                    } else {
                        entity = new DictEntity();
                        insertEntityList.add(entity);
                    }
                    entity.setCode(itemEnum.getCode());
                    entity.setTitle(itemEnum.getTitle());
                    entity.setBizType(itemEnum.getType());
                    entity.setIdx(itemEnum.getIdx());
                    entity.setStatus(StatusTypeEnum.ON.getValue());
                    entity.setScope(BizScopeTypeEnum.SYSTEM.getValue());
                    entity.setSource(SourceTypeEnum.SYSTEM.getValue());
                    entity.setActive(BooleanTypeEnum.TRUE.getValue());
                }

                this.dictService.insertBatch(insertEntityList);
                this.dictService.updateBatchById(updateEntityList);

                log.info("Initialize tenant [{}] dict done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }

        log.info("Initialize dict done.");
    }

    /**
     * @see DictApi#findByCode(DictRequest)
     */
    @Override
    public DictVo findByCode(DictRequest request) {
        return DictConverter.INSTANCE.entity2Vo(this.dictService.findByCode(request));
    }

    /**
     * @see DictApi#saveDict(DictSaveRequest)
     */
    @Override
    public void saveDict(DictSaveRequest request) {
        dictService.saveDict(request);
    }

    /**
     * @see DictApi#deleteByCode(DictRequest)
     */
    @Override
    public void deleteByCode(DictRequest request) {
        dictService.deleteByCode(request);
    }

    /**
     * @see DictApi#getDictType(BizTypeRequest)
     */
    @Override
    public DictTypeVo getDictType(BizTypeRequest request) {
        return dictService.getDictType(request);
    }

    @Override
    public Map<Long, RelationVo<DictVo>> relationMap(RelationRequest request) {
        return dictService.relationMap(request);
    }

    /**
     * @see DictApi#getRelation(RelationRequest)
     */
    @Override
    public RelationVo<DictVo> getRelation(RelationRequest request) {
        return dictService.getRelation(request);
    }

    /**
     * @see DictApi#saveRelation(RelationSaveRequest)
     */
    @Override
    public void saveRelation(RelationSaveRequest request) {
        dictRelationService.saveRelation(request);
    }

    /**
     * @see DictApi#deleteRelation(RelationRequest)
     */
    @Override
    public void deleteRelation(RelationRequest request) {
        dictRelationService.deleteRelation(request);
    }

    /**
     * @see DictApi#search(DictSearchRequest)
     */
    @Override
    public Page<DictVo> search(DictSearchRequest request) {
        return dictService.search(request);
    }

    /**
     * @see DictApi#list(DictSearchRequest)
     */
    @Override
    public List<DictVo> list(DictSearchRequest request) {
        return dictService.list(request);
    }

    /**
     * @see DictApi#getSequence(SequenceRequest)
     */
    @Override
    public SequenceVo getSequence(SequenceRequest request) {
        return dictSequenceService.getSequence(request);
    }

    /**
     * @see DictApi#saveSequence(SequenceRequest)
     */
    @Override
    public void saveSequence(SequenceRequest request) {
        dictSequenceService.saveSequence(request);
    }

    /**
     * @see DictApi#checkRelationExists(RelationRequest)
     */
    @Override
    public boolean checkRelationExists(RelationRequest request) {
        return dictService.checkRelationExists(request);
    }

    /**
     * @see DictApi#countRelations(RelationRequest)
     */
    @Override
    public long countRelations(RelationRequest request) {
        return dictService.countRelations(request);
    }

    /**
     * @see DictApi#resetSequence(SequenceRequest)
     */
    @Override
    public void resetSequence(SequenceRequest request) {
        dictService.resetSequence(request);
    }

    /**
     * @see DictApi#findDictWithSequence(SequenceRequest)
     */
    @Override
    public List<DictVo> findDictWithSequence(SequenceRequest request) {
        return dictService.findDictsWithSequence(request);
    }

}
