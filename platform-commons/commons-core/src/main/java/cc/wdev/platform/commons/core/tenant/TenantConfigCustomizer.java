package cc.wdev.platform.commons.core.tenant;

/**
 * @author elvea
 */
@FunctionalInterface
public interface TenantConfigCustomizer {

    void customize(TenantConfig config);

}
