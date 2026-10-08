package cc.wdev.platform.commons.core.tenant;

/**
 * @author elvea
 */
public class GlobalTenantManager {

    private static volatile TenantStore store = new DefaultTenantStore();

    private static volatile TenantConfig config = new TenantConfig();

    private static volatile TenantResolver resolver = new DefaultTenantResolver(store, config);

    public static TenantConfig getConfig() {
        return GlobalTenantManager.config;
    }

    public static void setConfig(TenantConfig config) {
        GlobalTenantManager.config = config;
    }

    public static TenantStore getStore() {
        return GlobalTenantManager.store;
    }

    public static void setStore(TenantStore store) {
        GlobalTenantManager.store = store;
    }

    public static TenantResolver getResolver() {
        return GlobalTenantManager.resolver;
    }

    public static void setResolver(TenantResolver resolver) {
        GlobalTenantManager.resolver = resolver;
    }

    public static void init(TenantStore store, TenantConfig config) {
        setConfig(config);
        setStore(store);
        setResolver(new DefaultTenantResolver(store, config));
    }

}
