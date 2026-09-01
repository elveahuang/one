package cc.wdev.platform.commons.autoconfigure.oapis;

import cc.wdev.platform.commons.autoconfigure.oapis.properties.TelegramProperties;
import cc.wdev.platform.commons.oapis.telegram.TelegramFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({TelegramProperties.class})
@ConditionalOnClass({TelegramOkHttpClientFactory.class})
@ConditionalOnProperty(prefix = TelegramProperties.PREFIX, name = "enabled", havingValue = "true")
public class TelegramAutoConfiguration {

    private final TelegramProperties properties;

    public TelegramAutoConfiguration(TelegramProperties properties) {
        log.info("TelegramAutoConfiguration is enabled");
        this.properties = properties;
    }

    @Bean(name = "telegramHttpClient")
    public OkHttpClient telegramHttpClient() {
        if (this.properties.getProxy().isEnabled()) {
            log.info("Create telegramHttpClient with proxy.");
            return new TelegramOkHttpClientFactory.SocksProxyOkHttpClientCreator(
                () -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(this.properties.getProxy().getHost(), this.properties.getProxy().getPort()))
            ).get();
        } else {
            log.info("Create telegramHttpClient without proxy.");
            return new TelegramOkHttpClientFactory.DefaultOkHttpClientCreator().get();
        }
    }

    @Bean(name = "telegramFactory")
    public TelegramFactory telegramFactory(TelegramProperties properties,
                                           @Qualifier("telegramHttpClient") OkHttpClient client) {
        return new TelegramFactory(properties.getProxy(), client, properties.getBot());
    }

}
