package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import cc.wdev.platform.commons.oapis.facebody.aliyun.AliyunFaceBodyService;
import cc.wdev.platform.commons.oapis.facebody.enums.FaceBodyTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(FaceBodyProperties.PREFIX)
public class FaceBodyProperties {

    public static final String PREFIX = "platform.face-body";

    private boolean enabled = false;

    private FaceBodyTypeEnum type = FaceBodyTypeEnum.Aliyun;

    @NestedConfigurationProperty
    private AliyunFaceBodyService.Config aliyun = new AliyunFaceBodyService.Config();

}
