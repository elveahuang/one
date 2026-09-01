package cc.wdev.platform.commons.oapis.facebody;

import cc.wdev.platform.commons.oapis.facebody.aliyun.AliyunFaceBodyService;
import cc.wdev.platform.commons.oapis.facebody.enums.FaceBodyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceBodyConfig {
    @Builder.Default
    private Boolean enabled = Boolean.FALSE;
    @Builder.Default
    private FaceBodyTypeEnum type = FaceBodyTypeEnum.Aliyun;
    @Builder.Default
    private AliyunFaceBodyService.Config aliyun = new AliyunFaceBodyService.Config();
}
