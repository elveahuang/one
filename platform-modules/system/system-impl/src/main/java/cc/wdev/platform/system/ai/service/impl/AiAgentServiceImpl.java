package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiAgentConverter;
import cc.wdev.platform.system.ai.domain.entity.AiAgentEntity;
import cc.wdev.platform.system.ai.domain.request.AiAgentGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiAgentSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiAgentSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.ai.repository.AiAgentRepository;
import cc.wdev.platform.system.ai.service.AiAgentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.AI_AGENT;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiAgentServiceImpl
    extends BaseCachingEntityService<AiAgentEntity, Long, AiAgentRepository>
    implements AiAgentService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(AI_AGENT);

    /**
     * @see CachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see AiAgentService#getAiAgent(AiAgentGetRequest)
     */
    @Override
    public AiAgentVo getAiAgent(AiAgentGetRequest request) {
        AiAgentEntity entity = null;
        if (StringUtils.isNotEmpty(request.getCode())) {
            entity = this.findCacheByCode(request.getCode().trim());
        } else if (ObjectUtils.isValidId(request.getId())) {
            entity = this.findCacheById(request.getId());
        }
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.AI_INVALID_AGENT);
        }
        return AiAgentConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiAgentService#findByPage(AiAgentSearchRequest)
     */
    @Override
    public Page<AiAgentVo> findByPage(AiAgentSearchRequest request) {
        IPage<AiAgentEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotBlank(request.getQ()), i -> i
                .like(AiAgentEntity::getCode, request.getQ())
                .or()
                .like(AiAgentEntity::getTitle, request.getQ())
            ).eq(StringUtils.isNotBlank(request.getCode()), AiAgentEntity::getCode, request.getCode())
            .eq(AiAgentEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiAgentVo> vos = page.getRecords().stream().map(AiAgentConverter.INSTANCE::entity2Vo).toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    /**
     * @see AiAgentService#saveAiAgent(AiAgentSaveRequest)
     */
    @Override
    public AiAgentEntity saveAiAgent(AiAgentSaveRequest request) {
        AiAgentEntity entity = this.findById(request.getId());
        if (entity == null) {
            entity = new AiAgentEntity();
        }
        entity.setCode(request.getCode());
        entity.setTitle(request.getTitle());
        entity.setSystemPrompt(request.getSystemPrompt());
        entity.setDescription(request.getDescription());
        entity.setDetails(request.getDetails());
        entity.setGreeting(request.getGreeting());
        entity.setStatus(request.getStatus());
        this.save(entity);
        return entity;
    }

    /**
     * @see AiAgentService#getAgents()
     */
    @Override
    public List<AiAgentSimpleVo> getAgents() {
        List<AiAgentEntity> entities = this.lambdaQueryWrapper()
            .select(AiAgentEntity::getId, AiAgentEntity::getCode, AiAgentEntity::getTitle)
            .eq(AiAgentEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiAgentEntity::getStatus, StatusTypeEnum.ON.getValue())
            .list();

        return entities.stream().map(e -> AiAgentSimpleVo.builder()
            .id(e.getId())
            .code(e.getCode())
            .title(e.getTitle())
            .build()
        ).toList();
    }

}
