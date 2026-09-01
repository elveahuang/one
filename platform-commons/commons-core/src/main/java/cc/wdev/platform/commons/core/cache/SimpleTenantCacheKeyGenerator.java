package cc.wdev.platform.commons.core.cache;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * @author elvea
 */
@Builder
public record SimpleTenantCacheKeyGenerator(String prefix) implements TenantCacheKeyGenerator {

    @NonNull
    @Override
    public String getPrefix() {
        return this.prefix;
    }

}
