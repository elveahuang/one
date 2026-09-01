package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiUsageEntity;
import cc.wdev.platform.system.ai.domain.request.AiUsageSearchRequest;
import org.springframework.data.domain.Page;

/**
 * AI 用量统计服务
 *
 * @author elvea
 */
public interface AiUsageService extends CachingEntityService<AiUsageEntity, Long> {

    /**
     * 记录对话用量
     */
    void recordChat(Long tenantId, Long userId, String modelName, Long kbId, String conversationId,
                    Integer promptTokens, Integer completionTokens, Integer totalTokens);

    /**
     * 记录调用次数型用量（SEARCH / EMBEDDING / RERANK）
     */
    void recordCall(Long tenantId, Long userId, String usageType, String modelName, Long kbId,
                    String conversationId, int callCount);

    /**
     * 分页查询用量
     */
    Page<AiUsageEntity> findByPage(AiUsageSearchRequest request);

}
