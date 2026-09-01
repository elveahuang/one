package cc.wdev.platform.system.core.cache;

import cc.wdev.platform.commons.core.cache.CacheKey;
import cc.wdev.platform.commons.core.cache.TenantCacheKeyGenerator;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import org.jetbrains.annotations.NotNull;

/**
 * @author elvea
 */
public class UserCacheKeyGenerator implements TenantCacheKeyGenerator {

    @Override
    public @NotNull String getPrefix() {
        return SystemCacheConstants.USER;
    }

    public CacheKey byUsername(String username) {
        return this.key("username", username);
    }

    public CacheKey byEmail(String email) {
        return this.key("email", email);
    }

    public CacheKey byMobile(String mobileCountryCode, String mobileNumber) {
        return this.key("mobile", mobileCountryCode, mobileNumber);
    }

}
