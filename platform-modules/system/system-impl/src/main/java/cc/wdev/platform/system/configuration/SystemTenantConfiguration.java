package cc.wdev.platform.system.configuration;

import cc.wdev.platform.commons.core.tenant.TenantConfigCustomizer;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SystemTenantConfiguration {

    /**
     * 系统模块中不需要租户隔离的表
     */
    @Bean("systemTenantConfigCustomizer")
    public TenantConfigCustomizer systemTenantConfigCustomizer() {
        return config -> {
            List<String> excludes = Lists.newArrayList();
            excludes.add("sys_biz_type");
            excludes.add("sys_authority");
            excludes.add("sys_client");
            excludes.add("sys_identity");
            excludes.add("sys_job");
            excludes.add("sys_config");
            excludes.add("sys_lang");
            excludes.add("sys_label");
            excludes.add("sys_package_item");
            excludes.add("sys_package");
            excludes.add("sys_tenant");
            excludes.add("sys_url_log");
            excludes.add("sys_region");
            excludes.add("sys_ai_agent");
            excludes.add("sys_ai_relation");
            excludes.add("sys_ai_mcp_server");
            excludes.add("sys_ai_tool");
            config.getExcludes().addAll(excludes);
        };
    }

}
