package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiKbChunkEntity;
import cc.wdev.platform.system.ai.repository.AiKbChunkRepository;
import cc.wdev.platform.system.ai.service.AiKbChunkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiKbChunkServiceImpl
    extends BaseEntityService<AiKbChunkEntity, Long, AiKbChunkRepository>
    implements AiKbChunkService {

    /**
     * @see AiKbChunkService#findByKbId(Long)
     */
    @Override
    public List<AiKbChunkEntity> findByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbId, kbId)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiKbChunkEntity::getKbItemId)
            .orderByAsc(AiKbChunkEntity::getChunkIndex)
            .list();
    }

    /**
     * @see AiKbChunkService#findByKbId(Long, int)
     */
    @Override
    public List<AiKbChunkEntity> findByKbId(Long kbId, int limit) {
        if (!ObjectUtils.isValidId(kbId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbId, kbId)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiKbChunkEntity::getKbItemId)
            .orderByAsc(AiKbChunkEntity::getChunkIndex)
            .list(MyBatisPlusUtils.getLimitPage(limit));
    }

    /**
     * @see AiKbChunkService#findByKbItemId(Long)
     */
    @Override
    public List<AiKbChunkEntity> findByKbItemId(Long kbItemId) {
        if (!ObjectUtils.isValidId(kbItemId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbItemId, kbItemId)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiKbChunkEntity::getChunkIndex)
            .list();
    }

    /**
     * @see AiKbChunkService#findByKbItemId(Long, int)
     */
    @Override
    public List<AiKbChunkEntity> findByKbItemId(Long kbItemId, int limit) {
        if (!ObjectUtils.isValidId(kbItemId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbItemId, kbItemId)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(AiKbChunkEntity::getChunkIndex)
            .list(MyBatisPlusUtils.getLimitPage(limit));
    }

    /**
     * @see AiKbChunkService#deleteByKbId(Long)
     */
    @Override
    public void deleteByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return;
        }
        List<AiKbChunkEntity> entities = this.findByKbId(kbId);
        if (CollectionUtils.isNotEmpty(entities)) {
            this.softDeleteBatch(entities);
        }
    }

    /**
     * @see AiKbChunkService#deleteByKbItemId(Long)
     */
    @Override
    public void deleteByKbItemId(Long kbItemId) {
        if (!ObjectUtils.isValidId(kbItemId)) {
            return;
        }
        List<AiKbChunkEntity> entities = this.findByKbItemId(kbItemId);
        if (CollectionUtils.isNotEmpty(entities)) {
            this.softDeleteBatch(entities);
        }
    }

    /**
     * @see AiKbChunkService#existsByContentHash(Long, String)
     */
    @Override
    public boolean existsByContentHash(Long kbId, String contentHash) {
        if (!ObjectUtils.isValidId(kbId) || StringUtils.isEmpty(contentHash)) {
            return false;
        }

        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbId, kbId)
            .eq(AiKbChunkEntity::getContentHash, contentHash)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count() > 0;
    }

    /**
     * @see AiKbChunkService#countByKbId(Long)
     */
    @Override
    public long countByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return 0L;
        }

        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbId, kbId)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

    /**
     * @see AiKbChunkService#findByHashes(Long, Collection)
     */
    @Override
    public List<AiKbChunkEntity> findByHashes(Long kbId, Collection<String> contentHashes) {
        if (!ObjectUtils.isValidId(kbId) || CollectionUtils.isEmpty(contentHashes)) {
            return List.of();
        }

        return this.lambdaQueryWrapper()
            .eq(AiKbChunkEntity::getKbId, kbId)
            .in(AiKbChunkEntity::getContentHash, contentHashes)
            .eq(AiKbChunkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

}
