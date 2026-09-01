package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.ai.enums.AiVectorizationStatus;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiKbItemConverter;
import cc.wdev.platform.system.ai.domain.entity.AiKbItemEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbItemResolveRequest;
import cc.wdev.platform.system.ai.domain.request.AiKbItemSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbItemVo;
import cc.wdev.platform.system.ai.repository.AiKbItemRepository;
import cc.wdev.platform.system.ai.service.AiKbItemService;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiKbItemServiceImpl
    extends BaseEntityService<AiKbItemEntity, Long, AiKbItemRepository>
    implements AiKbItemService {

    /**
     * @see AiKbItemService#getKbItem(GetRequest)
     */
    @Override
    public AiKbItemVo getKbItem(GetRequest request) {
        AiKbItemEntity entity = this.resolve(request.getId());
        return AiKbItemConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiKbItemService#getKbItem(GetRequest)
     */
    @Override
    public AiKbItemVo resolveKbItem(AiKbItemResolveRequest request) {
        AiKbItemEntity entity = null;
        if (ObjectUtils.isValidId(request.getId())) {
            entity = this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(AiKbItemEntity::getKbId, request.getKbId())
                .eq(AiKbItemEntity::getId, request.getId()));
        } else if (StringUtils.isNotEmpty(request.getBizType()) && ObjectUtils.isValidId(request.getBizId())) {
            entity = this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(AiKbItemEntity::getKbId, request.getKbId())
                .eq(AiKbItemEntity::getBizType, request.getBizType())
                .eq(AiKbItemEntity::getBizId, request.getBizId()));
        }
        return AiKbItemConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiKbItemService#findByKbId(Long)
     */
    @Override
    public List<AiKbItemEntity> findByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    /**
     * @see AiKbItemService#findByKbId(Long, int)
     */
    @Override
    public List<AiKbItemEntity> findByKbId(Long kbId, int limit) {
        if (!ObjectUtils.isValidId(kbId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiKbItemEntity::getId)
            .list(MyBatisPlusUtils.getLimitPage(Math.max(1, limit)));
    }

    /**
     * @see AiKbItemService#markCompleted(Long)
     */
    @Override
    public void markCompleted(Long id) {
        this.lambdaUpdateWrapper()
            .eq(AiKbItemEntity::getId, id)
            .set(AiKbItemEntity::getStatus, AiVectorizationStatus.COMPLETED.getValue())
            .update();
    }

    /**
     * @see AiKbItemService#markFailed(Long, String)
     */
    @Override
    public void markFailed(Long id, String error) {
        this.lambdaUpdateWrapper()
            .eq(AiKbItemEntity::getId, id)
            .set(AiKbItemEntity::getStatus, AiVectorizationStatus.FAILED.getValue())
            .update();
    }

    /**
     * @see AiKbItemService#deleteByKbId(Long)
     */
    @Override
    public void deleteByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return;
        }
        List<AiKbItemEntity> items = this.findByKbId(kbId);
        if (CollectionUtils.isNotEmpty(items)) {
            this.softDeleteBatch(items);
        }
    }

    /**
     * @see AiKbItemService#findByPage(AiKbItemSearchRequest)
     */
    @Override
    public Page<AiKbItemEntity> findByPage(AiKbItemSearchRequest request) {
        IPage<AiKbItemEntity> page = this.lambdaQueryWrapper()
            .eq(ObjectUtils.isValidId(request.getKbId()), AiKbItemEntity::getKbId, request.getKbId())
            .eq(StringUtils.isNotEmpty(request.getBizType()), AiKbItemEntity::getBizType, request.getBizType())
            .eq(request.getVectorized() != null, AiKbItemEntity::getVectorized, request.getVectorized())
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper -> wrapper
                .like(AiKbItemEntity::getTitle, request.getQ())
                .or()
                .like(AiKbItemEntity::getContent, request.getQ())
                .or()
                .like(AiKbItemEntity::getQuestion, request.getQ())
                .or()
                .like(AiKbItemEntity::getAnswer, request.getQ()))
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiKbItemEntity::getId)
            .page(MyBatisPlusUtils.getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see AiKbItemService#countByKbId(Long)
     */
    @Override
    public long countByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return 0L;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

    /**
     * @see AiKbItemService#countVectorizedByKbId(Long)
     */
    @Override
    public long countVectorizedByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return 0L;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getVectorized, BooleanTypeEnum.TRUE.getValue())
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

    /**
     * @see AiKbItemService#countPendingByKbId(Long)
     */
    @Override
    public long countPendingByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return 0L;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getStatus, AiVectorizationStatus.PENDING.getValue())
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

    /**
     * @see AiKbItemService#countFailedByKbId(Long)
     */
    @Override
    public long countFailedByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return 0L;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getStatus, AiVectorizationStatus.FAILED.getValue())
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

    /**
     * @see AiKbItemService#existsByContentHash(Long, String)
     */
    @Override
    public boolean existsByContentHash(Long kbId, String contentHash) {
        if (!ObjectUtils.isValidId(kbId) || StringUtils.isEmpty(contentHash)) {
            return false;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getContentHash, contentHash)
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count() > 0;
    }

    /**
     * @see AiKbItemService#groupCountByBizType(Long)
     */
    @Override
    public Map<String, Long> groupCountByBizType(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return Map.of();
        }
        return this.lambdaQueryWrapper()
            .select(AiKbItemEntity::getBizType)
            .eq(AiKbItemEntity::getKbId, kbId)
            .eq(AiKbItemEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list()
            .stream()
            .collect(Collectors.groupingBy(AiKbItemEntity::getBizType, Collectors.counting()));
    }

}
