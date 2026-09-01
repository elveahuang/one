package cc.wdev.platform.commons.ai.config;

import lombok.Data;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
public class ModelParentConfig implements Serializable {

    private @Nullable String apiKey;

    private @Nullable String baseUrl;

    public @Nullable String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(@Nullable String apiKey) {
        this.apiKey = apiKey;
    }

    public @Nullable String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(@Nullable String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
