package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.ai.utils.AiSecretUtils;
import cc.wdev.platform.commons.core.sequence.SequenceManager;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiApiKeyConverter;
import cc.wdev.platform.system.ai.domain.entity.AiApiKeyEntity;
import cc.wdev.platform.system.ai.domain.request.AiApiKeyRequest;
import cc.wdev.platform.system.ai.domain.request.AiApiKeySearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeySimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeyVo;
import cc.wdev.platform.system.ai.enums.AiApiKeyBizTypeEnum;
import cc.wdev.platform.system.ai.repository.AppKeyRepository;
import cc.wdev.platform.system.ai.service.AiApiKeyService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springaicommunity.mcp.security.server.apikey.memory.ApiKeyEntityImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

@Slf4j
@Service
public class AiApiKeyServiceImpl extends BaseCachingEntityService<AiApiKeyEntity, Long, AppKeyRepository> implements AiApiKeyService {

    /**
     * @see AiApiKeyService#findByKeyId(String)
     */
    @Override
    public ApiKeyEntity findByKeyId(@NonNull String keyId) {
        AiApiKeyEntity entity = this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(AiApiKeyEntity::getAppId, keyId)
            .eq(AiApiKeyEntity::getBizType, AiApiKeyBizTypeEnum.MCP_API_KEY.getValue())
            .eq(AiApiKeyEntity::getStatus, StatusTypeEnum.ON.getValue())
            .eq(AiApiKeyEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
        if (entity == null) {
            return null;
        }
        return ApiKeyEntityImpl.builder()
            .id(entity.getAppId())
            .secret(AiSecretUtils.decrypt(entity.getAppSecret()))
            .name(entity.getAppName())
            .build();
    }

    /**
     * @see AiApiKeyService#generate(AiApiKeyRequest)
     */
    @Override
    public AiApiKeySimpleVo generate(AiApiKeyRequest request) {
        AiApiKeyBizTypeEnum bizTypeEnum = BaseEnum.getEnumByValue(request.getBizType(), AiApiKeyBizTypeEnum.class);
        if (bizTypeEnum == null) {
            throw new RuntimeException("Invalid AI API Key Biz Type");
        }

        // 生产随机的应用ID和应用密钥
        String appId = SequenceManager.getSequence().generateCode();
        String appSecret = bizTypeEnum.getPrefix() + StringUtils.simpleUuid();

        // 保存应用
        save(AiApiKeyEntity.builder()
            .bizType(bizTypeEnum.getValue())
            .tenantId(TenantContext.getTenantId())
            .appId(appId)
            .appSecret(AiSecretUtils.encrypt(appSecret))
            .appName(request.getAppName())
            .description(request.getDescription())
            .build());

        return AiApiKeySimpleVo.builder()
            .appId(appId)
            .appName(request.getAppName())
            .appSecret(appSecret)
            .build();
    }

    @Override
    public Page<AiApiKeyVo> findAppKeyByPage(AiApiKeySearchRequest request) {
        IPage<AiApiKeyEntity> page = this.lambdaQueryWrapper()
            .like(StringUtils.isNotBlank(request.getQ()), AiApiKeyEntity::getDescription, request.getQ())
            .eq(AiApiKeyEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiApiKeyEntity::getTenantId, TenantContext.getTenantId())
            .page(getMyBatisPlusPage(request.getPageable()));
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiApiKeyVo> vos = page.getRecords().stream().map(entity -> {
            entity.setAppSecret(AiSecretUtils.decrypt(entity.getAppSecret()));
            return AiApiKeyConverter.INSTANCE.entity2Vo(entity);
        }).toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    @Override
    public void deleteAppKey(DeleteRequest request) {
        if (!CollectionUtils.isEmpty(Arrays.asList(request.getIds()))) {
            this.softDeleteBatchById(Arrays.asList(request.getIds()));
        }
    }

    @Override
    public void edit(AiApiKeyRequest request) {
        if (request.getId() == null) throw new RuntimeException("id有误");
        this.lambdaUpdateWrapper().eq(AiApiKeyEntity::getId, request.getId())
            .eq(AiApiKeyEntity::getTenantId, TenantContext.getTenantId())
            .eq(AiApiKeyEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .set(AiApiKeyEntity::getDescription, request.getDescription())
            .update();
    }

    @Override
    public AiApiKeyVo details(Long id) {
        AiApiKeyEntity entity = this.lambdaQueryWrapper().eq(AiApiKeyEntity::getId, id)
            .eq(AiApiKeyEntity::getTenantId, TenantContext.getTenantId())
            .eq(AiApiKeyEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .one();
        if (entity == null) {
            return null;
        }
        entity.setAppSecret(AiSecretUtils.decrypt(entity.getAppSecret()));
        return AiApiKeyConverter.INSTANCE.entity2Vo(entity);
    }
}
