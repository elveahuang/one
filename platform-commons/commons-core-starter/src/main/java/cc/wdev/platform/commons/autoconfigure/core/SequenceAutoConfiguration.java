package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.autoconfigure.core.properties.SequenceProperties;
import cc.wdev.platform.commons.core.sequence.Sequence;
import cc.wdev.platform.commons.core.sequence.SequenceManager;
import cc.wdev.platform.commons.core.sequence.snowflake.CosIdSnowflakeSequence;
import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosid.provider.IdGeneratorProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author elvea
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({SequenceProperties.class})
@ConditionalOnClass({IdGeneratorProvider.class})
@ConditionalOnProperty(prefix = SequenceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class SequenceAutoConfiguration {

    public SequenceAutoConfiguration() {
        log.info("SequenceAutoConfiguration is enabled");
    }

    @Bean
    @ConditionalOnMissingBean
    public Sequence sequence(IdGeneratorProvider provider) {
        Sequence sequence = new CosIdSnowflakeSequence(provider);
        SequenceManager.setSequence(sequence);
        return sequence;
    }

}
