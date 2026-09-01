package cc.wdev.platform.commons.core.cache;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * @author elvea
 */
@Builder
public record SimpleCacheKeyGenerator(String prefix) implements CacheKeyGenerator {

    @NonNull
    @Override
    public String getPrefix() {
        return this.prefix;
    }

    public static SimpleCacheKeyGenerator byClassSimpleName(Class<?> o) {
        return new SimpleCacheKeyGenerator(o.getSimpleName().toLowerCase());
    }

}
