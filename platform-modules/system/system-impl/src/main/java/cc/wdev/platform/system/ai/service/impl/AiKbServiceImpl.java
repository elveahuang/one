package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiKbConverter;
import cc.wdev.platform.system.ai.domain.entity.AiKbEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiKbSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.repository.AiKbRepository;
import cc.wdev.platform.system.ai.service.AiKbService;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.AI_KB;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiKbServiceImpl
    extends BaseCachingEntityService<AiKbEntity, Long, AiKbRepository>
    implements AiKbService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(AI_KB);

    /**
     * @see AiKbService#getKb(GetRequest)
     */
    @Override
    public AiKbVo getKb(GetRequest request) {
        AiKbEntity entity = resolve(request.getId(), request.getCode());
        return AiKbConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiKbService#saveKb(AiKbSaveRequest)
     */
    @Override
    public AiKbEntity saveKb(AiKbSaveRequest request) {
        AiKbEntity entity = this.findById(request.getId());
        if (entity == null) {
            entity = new AiKbEntity();
        }
        entity.setCode(request.getCode());
        entity.setTitle(request.getTitle());
        entity.setCollectionName(StringUtils.nvl(request.getCollectionName(), request.getCode()));
        entity.setDetails(request.getDetails());
        entity.setDescription(request.getDescription());
        if (request.getRetrievalConfig() != null) {
            entity.setTopK(request.getRetrievalConfig().getTopK());
            entity.setSimilarityThreshold(request.getRetrievalConfig().getSimilarityThreshold());
        }
        entity.setChunkSize(request.getChunkSize() != null && request.getChunkSize() > 0
            ? request.getChunkSize() : entity.getChunkSize());
        entity.setChunkOverlap(request.getChunkOverlap() != null && request.getChunkOverlap() > 0
            ? request.getChunkOverlap() : entity.getChunkOverlap());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : StatusTypeEnum.ON.getValue());
        this.save(entity);
        return entity;
    }

    /**
     * @see AiKbService#getKbs()
     */
    @Override
    public List<AiKbEntity> getKbs() {
        return this.lambdaQueryWrapper()
            .select(AiKbEntity::getId, AiKbEntity::getCode, AiKbEntity::getTitle)
            .eq(AiKbEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiKbEntity::getStatus, StatusTypeEnum.ON.getValue())
            .list();
    }

    /**
     * @see AiKbService#findByPage(AiKbSearchRequest)
     */
    @Override
    public Page<AiKbEntity> findByPage(AiKbSearchRequest request) {
        IPage<AiKbEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper -> wrapper
                .like(AiKbEntity::getTitle, request.getQ())
                .or()
                .like(AiKbEntity::getCode, request.getQ()))
            .eq(request.getStatus() != null, AiKbEntity::getStatus, request.getStatus())
            .eq(AiKbEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiKbEntity::getId)
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see cc.wdev.platform.commons.service.CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see AiKbService#findByCollectionName(String)
     */
    @Override
    public AiKbEntity findByCollectionName(String collectionName) {
        if (StringUtils.isEmpty(collectionName)) {
            return null;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbEntity::getCollectionName, collectionName.trim())
            .eq(AiKbEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .one();
    }

}
