package cc.wdev.platform.commons.autoconfigure.core.properties;

import cc.wdev.platform.commons.core.GlobalContext;
import cc.wdev.platform.commons.core.tenant.TenantConfig;
import cc.wdev.platform.commons.message.rabbit.RabbitConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@ConfigurationProperties(CoreProperties.PREFIX)
public class CoreProperties implements Serializable {

    public static final String PREFIX = "platform";

    public static final String TENANCY_PREFIX = PREFIX + ".tenancy";

    /**
     * 基本信息
     */
    @NestedConfigurationProperty
    private GlobalContext.App app = GlobalContext.App.builder().build();

    /**
     * 调试模式
     */
    @NestedConfigurationProperty
    private GlobalContext.Debug debug = GlobalContext.Debug.builder().build();

    /**
     * 主页配置
     */
    @NestedConfigurationProperty
    private GlobalContext.Home home = GlobalContext.Home.builder().build();

    /**
     * 消息队列
     */
    @NestedConfigurationProperty
    private RabbitConfig rabbit = RabbitConfig.builder().build();

    /**
     * 多租户配置
     */
    @NestedConfigurationProperty
    private TenantConfig tenancy = TenantConfig.builder().build();

}
