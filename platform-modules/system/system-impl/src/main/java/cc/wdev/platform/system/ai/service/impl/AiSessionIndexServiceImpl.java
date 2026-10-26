package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiSessionIndexEntity;
import cc.wdev.platform.system.ai.repository.AiSessionIndexRepository;
import cc.wdev.platform.system.ai.service.AiSessionIndexService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiSessionIndexServiceImpl
    extends BaseCachingEntityService<AiSessionIndexEntity, Long, AiSessionIndexRepository>
    implements AiSessionIndexService {

    /**
     * 与 JdbcSessionRepository 保持一致；MySQL 下可保证复合索引不超过 InnoDB 键长限制。
     */
    private static final int MAX_INDEXED_VALUE_LENGTH = 512;

    @Override
    public List<AiSessionIndexEntity> findBySessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionIndexEntity::getSessionId, sessionId)
            .orderByAsc(AiSessionIndexEntity::getAttrName)
            .list();
    }

    @Override
    public List<AiSessionIndexEntity> findByAttrNameAndValue(String attrName, String attrValue) {
        if (!StringUtils.hasText(attrName) || attrValue == null) {
            return List.of();
        }
        return this.lambdaQueryWrapper()
            .eq(AiSessionIndexEntity::getAttrName, attrName)
            .eq(AiSessionIndexEntity::getAttrValue, indexValue(attrValue))
            .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceSessionIndexes(String sessionId, Long tenantId, Map<String, Object> metadata,
                                      Set<String> indexedAttributes) {
        Assert.hasText(sessionId, "sessionId must not be null or empty");
        Assert.notNull(metadata, "metadata must not be null");
        Assert.notNull(indexedAttributes, "indexedAttributes must not be null");

        this.deleteBySessionId(sessionId);
        List<AiSessionIndexEntity> indexes = indexedAttributes.stream()
            .filter(attrName -> StringUtils.hasText(attrName) && metadata.get(attrName) != null)
            .map(attrName -> Map.entry(attrName, metadata.get(attrName)))
            .map(entry -> AiSessionIndexEntity.builder()
                .tenantId(tenantId != null ? tenantId : 0L)
                .sessionId(sessionId)
                .attrName(entry.getKey())
                .attrValue(indexValue(String.valueOf(entry.getValue())))
                .build())
            .toList();
        if (!indexes.isEmpty()) {
            this.saveBatch(indexes);
        }
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return;
        }
        this.getMapper().delete(Wrappers.<AiSessionIndexEntity>lambdaQuery()
            .eq(AiSessionIndexEntity::getSessionId, sessionId));
    }

    /**
     * 参考实现的索引值只保留前 512 个字符，命中后再由完整 metadata 精确校验。
     */
    private static String indexValue(String value) {
        if (value.length() <= MAX_INDEXED_VALUE_LENGTH) {
            return value;
        }
        int cut = Character.isHighSurrogate(value.charAt(MAX_INDEXED_VALUE_LENGTH - 1))
            ? MAX_INDEXED_VALUE_LENGTH - 1
            : MAX_INDEXED_VALUE_LENGTH;
        return value.substring(0, cut);
    }

}
