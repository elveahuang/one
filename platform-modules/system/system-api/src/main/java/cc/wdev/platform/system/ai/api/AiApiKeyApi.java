package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiApiKeyRequest;
import cc.wdev.platform.system.ai.domain.request.AiApiKeySearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeySimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeyVo;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface AiApiKeyApi {

    ApiKeyEntity findByKeyId(@NonNull String keyId);

    /**
     * 生成密钥
     */
    AiApiKeySimpleVo generate(AiApiKeyRequest appKeyGenerateRequest);

    /**
     * 分页查询密钥
     */
    Page<AiApiKeyVo> findByPage(AiApiKeySearchRequest request);

    /**
     * 删除密钥
     */
    void deleteApiKey(DeleteRequest request);

    /**
     * 修改密钥
     */
    void edit(AiApiKeyRequest request);

    /**
     * 获取密钥详情
     */
    AiApiKeyVo details(Long id);

}
