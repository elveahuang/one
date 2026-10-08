package cc.wdev.platform.commons.autoconfigure.data.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(MyBatisCustomProperties.PREFIX)
public class MyBatisCustomProperties {

    public static final String PREFIX = "platform.data.mybatis";

    public static final String IDENTIFIER_GENERATOR_PREFIX = PREFIX + ".identifier-generator";

    public static final String META_OBJECT_HANDLER_PREFIX = PREFIX + ".meta-object-handler";

    private boolean enabled = false;

    private boolean showSql = true;

    @NestedConfigurationProperty
    private IdentifierGeneratorConfig identifierGenerator = IdentifierGeneratorConfig.builder().build();

    @NestedConfigurationProperty
    private MetaObjectHandlerConfig metaObjectHandler = MetaObjectHandlerConfig.builder().build();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentifierGeneratorConfig {

        @Builder.Default
        private boolean enabled = true;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaObjectHandlerConfig {

        @Builder.Default
        private boolean enabled = true;

    }

}
