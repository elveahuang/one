package cc.wdev.platform.commons.oapis.facebody;

import cc.wdev.platform.commons.oapis.facebody.aliyun.AliyunFaceBodyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public record FaceBodyFactory(FaceBodyConfig config) {

    public FaceBodyService getFaceBodyService() {
        return getAliyunFaceBodyService();
    }

    public FaceBodyService getAliyunFaceBodyService() {
        return getAliyunFaceBodyService(this.config.getAliyun());
    }

    public FaceBodyService getAliyunFaceBodyService(AliyunFaceBodyService.Config config) {
        return new AliyunFaceBodyService(config);
    }

}
