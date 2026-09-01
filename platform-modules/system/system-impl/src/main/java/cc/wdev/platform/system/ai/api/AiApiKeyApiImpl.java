package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.system.ai.domain.request.AiApiKeyRequest;
import cc.wdev.platform.system.ai.domain.request.AiApiKeySearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeySimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiApiKeyVo;
import cc.wdev.platform.system.ai.service.AiApiKeyService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiApiKeyApiImpl implements AiApiKeyApi {

    private final AiApiKeyService aiApiKeyService;

    /**
     * @see AiApiKeyApi#findByKeyId(String)
     */
    @Override
    public ApiKeyEntity findByKeyId(@NonNull String keyId) {
        return this.aiApiKeyService.findByKeyId(keyId);
    }

    @Override
    public AiApiKeySimpleVo generate(AiApiKeyRequest appKeyGenerateRequest) {
        return aiApiKeyService.generate(appKeyGenerateRequest);
    }

    @Override
    public Page<AiApiKeyVo> findByPage(AiApiKeySearchRequest request) {
        return aiApiKeyService.findAppKeyByPage(request);
    }

    @Override
    public void deleteApiKey(DeleteRequest request) {
        aiApiKeyService.deleteAppKey(request);
    }

    @Override
    public void edit(AiApiKeyRequest request) {
        aiApiKeyService.edit(request);
    }

    @Override
    public AiApiKeyVo details(Long id) {
        return aiApiKeyService.details(id);
    }

}
