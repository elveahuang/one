package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiApiKeyEntity;
import cc.wdev.platform.system.ai.domain.request.AiApiKeyRequest;
import cc.wdev.platform.system.ai.domain.request.AiApiKeySearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeySimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeyVo;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springframework.data.domain.Page;

public interface AiApiKeyService extends CachingEntityService<AiApiKeyEntity, Long> {

    ApiKeyEntity findByKeyId(@NonNull String keyId);

    /**
     * 生成密钥
     */
    AiApiKeySimpleVo generate(AiApiKeyRequest request);

    /**
     * 查询appkey列表
     */
    Page<AiApiKeyVo> findAppKeyByPage(AiApiKeySearchRequest request);

    /**
     * 删除appkey
     */
    void deleteAppKey(DeleteRequest request);

    /**
     * 编辑appkey密钥描述
     */
    void edit(AiApiKeyRequest request);

    /**
     * 获取appkey详情
     */
    AiApiKeyVo details(Long id);
}
