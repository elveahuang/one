package cc.wdev.platform.commons.autoconfigure.data;

import cc.wdev.platform.commons.autoconfigure.data.properties.ElasticsearchCustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchAutoConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ElasticsearchCustomProperties.class)
@ConditionalOnClass({DataElasticsearchAutoConfiguration.class})
@ConditionalOnProperty(prefix = ElasticsearchCustomProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableElasticsearchAuditing
@EnableElasticsearchRepositories
public class ElasticsearchCustomAutoConfiguration {

    public ElasticsearchCustomAutoConfiguration() {
        log.info("ElasticsearchCustomAutoConfiguration is enabled");
    }

}
