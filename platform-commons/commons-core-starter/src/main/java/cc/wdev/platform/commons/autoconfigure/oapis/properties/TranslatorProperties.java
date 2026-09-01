package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import cc.wdev.platform.commons.oapis.translator.aliyun.AliyunTranslator;
import cc.wdev.platform.commons.oapis.translator.enums.TranslatorTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = TranslatorProperties.PREFIX)
public class TranslatorProperties implements Serializable {

    public static final String PREFIX = "platform.translator";

    private boolean enabled = false;

    private TranslatorTypeEnum type = TranslatorTypeEnum.Aliyun;

    @NestedConfigurationProperty
    private AliyunTranslator.Config aliyun = new AliyunTranslator.Config();

}
