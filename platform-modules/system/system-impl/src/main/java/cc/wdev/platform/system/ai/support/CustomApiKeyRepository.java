package cc.wdev.platform.system.ai.support;

import cc.wdev.platform.system.ai.api.AiApiKeyApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntity;
import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntityRepository;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class CustomApiKeyRepository implements ApiKeyEntityRepository<ApiKeyEntity> {

    private final AiApiKeyApi aiApiKeyApi;

    /**
     * @see ApiKeyEntityRepository#findByKeyId(String)
     */
    @Override
    public ApiKeyEntity findByKeyId(@NonNull String keyId) {
        return this.aiApiKeyApi.findByKeyId(keyId);
    }

}
