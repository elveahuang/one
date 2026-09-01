package cc.wdev.platform.commons.core.listener;

import cc.wdev.platform.commons.core.tenant.GlobalTenantManager;
import cc.wdev.platform.commons.core.tenant.TenantConfig;
import cc.wdev.platform.commons.core.tenant.TenantStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class CoreStartupListener {
    private final TenantStore tenantStore;
    private final TenantConfig tenantConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Initializing GlobalTenantManager");

        // 初始化全局多租户管理器
        GlobalTenantManager.init(tenantStore, tenantConfig);
    }

}
