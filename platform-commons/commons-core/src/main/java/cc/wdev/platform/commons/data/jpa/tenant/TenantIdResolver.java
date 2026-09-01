package cc.wdev.platform.commons.data.jpa.tenant;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * @author elvea
 */
@Slf4j
public class TenantIdResolver implements CurrentTenantIdentifierResolver<Long> {

    /**
     * @see CurrentTenantIdentifierResolver#resolveCurrentTenantIdentifier()
     */
    @Override
    public Long resolveCurrentTenantIdentifier() {
        return TenantContext.getTenantId();
    }

    /**
     * @see CurrentTenantIdentifierResolver#validateExistingCurrentSessions()
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
