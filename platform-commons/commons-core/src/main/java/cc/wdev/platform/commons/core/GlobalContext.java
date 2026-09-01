package cc.wdev.platform.commons.core;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.constants.GlobalConstants;
import cc.wdev.platform.commons.core.tenant.TenantConfig;
import cc.wdev.platform.commons.message.rabbit.RabbitConfig;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.time.ZoneId;

/**
 * @author elvea
 */
@Builder
public class GlobalContext implements Serializable, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Setter
    @Getter
    @Builder.Default
    private GlobalContext.App app = GlobalContext.App.builder().build();

    @Setter
    @Getter
    @Builder.Default
    private GlobalContext.Debug debug = GlobalContext.Debug.builder().build();

    @Setter
    @Getter
    @Builder.Default
    private GlobalContext.Home home = GlobalContext.Home.builder().build();

    @Setter
    @Getter
    @Builder.Default
    private GlobalContext.MapStruct mapStruct = GlobalContext.MapStruct.builder().build();

    @Setter
    @Getter
    @Builder.Default
    private RabbitConfig rabbit = RabbitConfig.builder().build();

    @Setter
    @Getter
    @Builder.Default
    private TenantConfig tenancy = TenantConfig.builder().build();

    /**
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Data
    @Builder
    public static class Home implements Serializable {

        @Builder.Default
        private String main = "";

        @Builder.Default
        private String webapp = "";

        @Builder.Default
        private String mobile = "";

    }

    @Data
    @Builder
    public static class Debug implements Serializable {

        /**
         * 是否开启调试模式
         */
        @Builder.Default
        private boolean enabled = false;

    }

    @Data
    @Builder
    public static class MapStruct implements Serializable {

        private String componentModel;

    }

    @Data
    @Builder
    public static class App implements Serializable {

        @Builder.Default
        private String applicationName = GlobalConstants.NAME;

        @Builder.Default
        private String applicationVersion = GlobalConstants.VERSION;

        @Builder.Default
        private ZoneId userZoneId = DateTimeConstants.ZONE_ID_DEFAULT;

        @Builder.Default
        private ZoneId systemZoneId = DateTimeConstants.ZONE_ID_DEFAULT;

        @Builder.Default
        private String language = "zh_CN";

    }

}
