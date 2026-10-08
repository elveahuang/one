package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiUsageEntity;
import cc.wdev.platform.system.ai.domain.request.AiUsageSearchRequest;
import cc.wdev.platform.system.ai.repository.AiUsageRepository;
import cc.wdev.platform.system.ai.service.AiUsageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * AI 用量统计服务实现
 *
 * @author elvea
 */
@Slf4j
@Service
public class AiUsageServiceImpl
    extends BaseCachingEntityService<AiUsageEntity, Long, AiUsageRepository>
    implements AiUsageService {

    /**
     * @see AiUsageService#recordChat(Long, Long, String, Long, String, Integer, Integer, Integer)
     */
    @Override
    public void recordChat(Long tenantId, Long userId, String modelName, Long kbId, String conversationId,
                           Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        AiUsageEntity entity = AiUsageEntity.builder()
            .userId(userId)
            .usageType("CHAT")
            .modelName(modelName)
            .kbId(kbId)
            .conversationId(conversationId)
            .promptTokens(promptTokens != null ? promptTokens : 0)
            .completionTokens(completionTokens != null ? completionTokens : 0)
            .totalTokens(totalTokens != null ? totalTokens : 0)
            .callCount(1)
            .build();
        entity.setTenantId(tenantId);
        this.record(entity);
    }

    /**
     * @see AiUsageService#recordCall(Long, Long, String, String, Long, String, int)
     */
    @Override
    public void recordCall(Long tenantId, Long userId, String usageType, String modelName, Long kbId,
                           String conversationId, int callCount) {
        AiUsageEntity entity = AiUsageEntity.builder()
            .userId(userId)
            .usageType(usageType)
            .modelName(modelName)
            .kbId(kbId)
            .conversationId(conversationId)
            .promptTokens(0)
            .completionTokens(0)
            .totalTokens(0)
            .callCount(callCount)
            .build();
        entity.setTenantId(tenantId);
        this.record(entity);
    }

    /**
     * @see AiUsageService#findByPage(AiUsageSearchRequest)
     */
    @Override
    public Page<AiUsageEntity> findByPage(AiUsageSearchRequest request) {
        IPage<AiUsageEntity> page = this.lambdaQueryWrapper()
            .eq(ObjectUtils.isValidId(request.getKbId()), AiUsageEntity::getKbId, request.getKbId())
            .eq(ObjectUtils.isValidId(request.getUserId()), AiUsageEntity::getUserId, request.getUserId())
            .eq(StringUtils.isNotEmpty(request.getUsageType()), AiUsageEntity::getUsageType, request.getUsageType())
            .eq(AiUsageEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiUsageEntity::getId)
            .page(MyBatisPlusUtils.getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    private void record(AiUsageEntity entity) {
        try {
            this.save(entity);
        } catch (Exception e) {
            // 用量统计失败不影响主流程
            log.warn("Record AI usage failed, type={}, model={}", entity.getUsageType(), entity.getModelName(), e);
        }
    }

}
