package cc.wdev.platform.commons.autoconfigure.extensions;

import cc.wdev.platform.commons.autoconfigure.extensions.properties.IpProperties;
import cc.wdev.platform.commons.extensions.ip.GlobalIpManager;
import cc.wdev.platform.commons.extensions.ip.LocationEnum;
import cc.wdev.platform.commons.extensions.ip.geoip2.GeoLite;
import cc.wdev.platform.commons.extensions.ip.ip2region.Ip2Region;
import cc.wdev.platform.commons.utils.StringUtils;
import com.maxmind.db.Metadata;
import com.maxmind.db.Network;
import com.maxmind.db.Networks;
import com.maxmind.db.Reader;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.function.Consumer;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = IpProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties({IpProperties.class})
@ImportRuntimeHints({IpAutoConfiguration.IpRuntimeHints.class})
public class IpAutoConfiguration {

    public IpAutoConfiguration(IpProperties properties) {
        log.info("IpAutoConfiguration is enabled");
        log.info("GeoLite location - {}", properties.getGeoLite().getLocation());
        log.info("GeoLite path - {}", properties.getGeoLite().getPath());
    }

    @Bean
    @ConditionalOnClass({GeoLite.class})
    @ConditionalOnMissingBean
    public GeoLite geoLite(IpProperties properties) {
        GeoLite geoLite = new GeoLite();
        if (StringUtils.isNotEmpty(properties.getGeoLite().getPath())) {
            Resource resource;
            if (LocationEnum.CLASSPATH.equals(properties.getGeoLite().getLocation())) {
                resource = new ClassPathResource(properties.getGeoLite().getPath());
            } else {
                resource = new FileSystemResource(properties.getGeoLite().getPath());
            }
            if (resource.exists()) {
                geoLite.init(resource);
            } else {
                log.info("GeoLite file not exists - {}", properties.getGeoLite().getPath());
            }
        }

        GlobalIpManager.setGeoLite(geoLite);
        return geoLite;
    }

    @Bean
    @ConditionalOnClass({Searcher.class})
    @ConditionalOnMissingBean
    public Ip2Region ip2Region(IpProperties properties) {
        Ip2Region ip2Region = new Ip2Region();
        if (StringUtils.isNotEmpty(properties.getIp2region().getPath())) {
            Resource resource;
            if (LocationEnum.CLASSPATH.equals(properties.getIp2region().getLocation())) {
                resource = new ClassPathResource(properties.getIp2region().getPath());
            } else {
                resource = new FileSystemResource(properties.getIp2region().getPath());
            }
            if (resource.exists()) {
                ip2Region.init(resource);
            } else {
                log.info("Ip2Region file not exists - {}", properties.getIp2region().getPath());
            }
        }

        GlobalIpManager.setIp2Region(ip2Region);
        return ip2Region;
    }

    public static class IpRuntimeHints implements RuntimeHintsRegistrar {

        private static final Consumer<TypeHint.Builder> INVOKE_DECLARED_CONSTRUCTORS = TypeHint.builtWith(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(Metadata.class, INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerType(Network.class, INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerType(Networks.class, INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerType(Reader.class, INVOKE_DECLARED_CONSTRUCTORS);
        }

    }

}
