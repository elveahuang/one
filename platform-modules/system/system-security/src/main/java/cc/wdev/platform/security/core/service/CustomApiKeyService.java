package cc.wdev.platform.security.core.service;

import cc.wdev.platform.system.ai.api.AiApiKeyApi;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntityRepository;

@RequiredArgsConstructor
public class CustomApiKeyService implements ApiKeyEntityRepository<ApiKeyEntity> {

    private final AiApiKeyApi aiApiKeyApi;

    /**
     * @see ApiKeyEntityRepository#findByKeyId(String)
     */
    @Override
    public ApiKeyEntity findByKeyId(@NonNull String keyId) {
        return this.aiApiKeyApi.findByKeyId(keyId);
    }

}
