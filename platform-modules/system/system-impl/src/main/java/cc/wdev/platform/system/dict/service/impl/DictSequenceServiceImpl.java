package cc.wdev.platform.system.dict.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.dict.domain.entity.DictSequenceEntity;
import cc.wdev.platform.system.dict.repository.DictSequenceRepository;
import cc.wdev.platform.system.dict.service.DictSequenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author erden
 */
@Slf4j
@Service
@AllArgsConstructor
public class DictSequenceServiceImpl
    extends BaseCachingEntityService<DictSequenceEntity, Long, DictSequenceRepository>
    implements DictSequenceService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleCacheKeyGenerator("dict-sequence");

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see DictSequenceService#saveSequence(SequenceRequest)
     */
    @Override
    public void saveSequence(SequenceRequest request) {
        if (ObjectUtils.isValidId(request.getBizId()) && StringUtils.isNotEmpty(request.getBizType())) {
            // 删除旧的排序
            deleteSequence(request);
        }

        if (request.getSequence() != null && !request.getSequence().isEmpty()) {
            List<DictSequenceEntity> entityList = new ArrayList<>(request.getSequence().size());
            for (Map.Entry<Long, Integer> entry : request.getSequence().entrySet()) {
                DictSequenceEntity entity = DictSequenceEntity.builder()
                    .bizType(request.getBizType())
                    .bizId(request.getBizId())
                    .dictId(entry.getKey())
                    .idx(entry.getValue())
                    .build();
                entityList.add(entity);
            }
            if (CollectionUtils.isNotEmpty(entityList)) {
                saveBatch(entityList);
                log.info("DictSequence Save success. bizType={}, bizId={}, size={}", request.getBizType(), request.getBizId(), entityList.size());
            }
        }
    }

    private void deleteSequence(SequenceRequest request) {
        lambdaUpdateWrapper()
            .eq(DictSequenceEntity::getBizType, request.getBizType())
            .eq(DictSequenceEntity::getBizId, request.getBizId())
            .remove();
    }

    /**
     * @see DictSequenceService#getSequence(SequenceRequest)
     */
    @Override
    public SequenceVo getSequence(SequenceRequest request) {
        List<DictSequenceEntity> list = findSequence(request);
        Map<Long, Integer> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (DictSequenceEntity e : list) {
                map.put(e.getDictId(), e.getIdx());
            }
        }
        return SequenceVo.builder()
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .sequence(map)
            .build();
    }

    @Override
    public List<DictSequenceEntity> findSequence(SequenceRequest request) {
        return lambdaQueryWrapper()
            .eq(DictSequenceEntity::getBizType, request.getBizType())
            .eq(DictSequenceEntity::getBizId, request.getBizId())
            .list();
    }
}
