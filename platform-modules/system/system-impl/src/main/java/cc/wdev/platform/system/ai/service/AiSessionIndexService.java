package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiSessionIndexEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AI Session 元数据索引服务。
 *
 * @author elvea
 */
public interface AiSessionIndexService extends CachingEntityService<AiSessionIndexEntity, Long> {

    /**
     * 查询会话的全部索引项。
     */
    List<AiSessionIndexEntity> findBySessionId(String sessionId);

    /**
     * 按属性名和属性值查询候选会话。
     */
    List<AiSessionIndexEntity> findByAttrNameAndValue(String attrName, String attrValue);

    /**
     * 全量替换会话的索引项。
     */
    void replaceSessionIndexes(String sessionId, Long tenantId, Map<String, Object> metadata,
                               Set<String> indexedAttributes);

    /**
     * 删除会话的全部索引项。
     */
    void deleteBySessionId(String sessionId);

}
