package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiMcpServerConverter;
import cc.wdev.platform.system.ai.domain.entity.AiMcpServerEntity;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiMcpServerSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiMcpServerVo;
import cc.wdev.platform.system.ai.repository.AiMcpServerRepository;
import cc.wdev.platform.system.ai.service.AiMcpServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.AI_MCP_SERVER;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiMcpServerServiceImpl extends BaseCachingEntityService<AiMcpServerEntity, Long, AiMcpServerRepository> implements AiMcpServerService {

    private static final String MCP_CODE_PREFIX = "MCP";

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(AI_MCP_SERVER);

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    @Override
    public AiMcpServerVo getAiMcpServer(Long id) {
        if (null == id || id <= 0) {
            return new AiMcpServerVo();
        }
        AiMcpServerEntity entity = this.findByCacheKey(cacheKeyGenerator.byId(id), key -> super.findById(id));
        if (null == entity) {
            return new AiMcpServerVo();
        }
        return AiMcpServerConverter.INSTANCE.entityVo(entity);
    }

    @Override
    public AiMcpServerVo getAiMcpServerByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        AiMcpServerEntity entity = this.findByCacheKey(cacheKeyGenerator.byCode(code), _ -> this.findOneByWrapper(
            this.lambdaQueryWrapper().eq(AiMcpServerEntity::getCode, code)
                .eq(AiMcpServerEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .eq(AiMcpServerEntity::getStatus, StatusTypeEnum.ON.getValue())
        ));
        if (null == entity) {
            return null;
        }
        return AiMcpServerConverter.INSTANCE.entityVo(entity);
    }

    @Override
    public void deleteAiMcpServer(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.softDeleteBatchById(ids);
    }

    @Override
    public void saveAiMcpServer(AiMcpServerSaveRequest request) {
        if (null == request) {
            return;
        }
        AiMcpServerEntity entity = AiMcpServerConverter.INSTANCE.form2Entity(request);
        if (null != request.getId() && request.getId() > 0) {
            entity.setId(request.getId());
        }
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(generateCode(MCP_CODE_PREFIX));
        }
        this.save(entity);
    }

    @Override
    public Page<AiMcpServerVo> findByPage(AiMcpServerSearchRequest request) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AiMcpServerEntity> page = this.lambdaQueryWrapper()
            .eq(request.getUrl() != null, AiMcpServerEntity::getUrl, request.getUrl())
            .eq(StringUtils.isNotBlank(request.getCode()), AiMcpServerEntity::getCode, request.getCode())
            .like(StringUtils.isNotBlank(request.getQ()), AiMcpServerEntity::getTitle, request.getQ())
            .eq(AiMcpServerEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiMcpServerVo> vos = page.getRecords().stream().map(AiMcpServerConverter.INSTANCE::entityVo).toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }
}
