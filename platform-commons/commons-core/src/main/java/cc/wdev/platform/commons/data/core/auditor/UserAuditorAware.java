package cc.wdev.platform.commons.data.core.auditor;

import cc.wdev.platform.commons.utils.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author elvea
 * @see AuditorAware
 */
public class UserAuditorAware implements AuditorAware<Long> {

    /**
     * @see AuditorAware#getCurrentAuditor()
     */
    public @NotNull Optional<Long> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getUid());
    }

}
