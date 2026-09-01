package cc.wdev.platform.commons.oapis.facebody.aliyun;

import cc.wdev.platform.commons.oapis.facebody.FaceBodyResult;
import cc.wdev.platform.commons.oapis.facebody.FaceBodyService;
import cc.wdev.platform.commons.utils.JacksonUtils;
import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import jodd.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Collections;

/**
 * 当前阿里云视觉智能开放平台只支持上海区
 *
 * @author elvea
 */
@Slf4j
public class AliyunFaceBodyService implements FaceBodyService {

    private final Config config;

    public AliyunFaceBodyService(Config config) {
        this.config = config;
    }

    @Override
    public FaceBodyResult detectFace(String image) {
        try {
            DetectLivingFaceAdvanceRequest.DetectLivingFaceAdvanceRequestTasks task = new DetectLivingFaceAdvanceRequest.DetectLivingFaceAdvanceRequestTasks();
            task.setImageURLObject(new ByteArrayInputStream(Base64.decode(image)));

            DetectLivingFaceAdvanceRequest request = new DetectLivingFaceAdvanceRequest().setTasks(Collections.singletonList(task));
            DetectLivingFaceResponse response = getClient().detectLivingFaceAdvance(request, getRuntime());

            String responseJson = JacksonUtils.toJson(TeaModel.buildMap(response));
            log.info("AliyunFaceBodyService.detectFace response - {}.", responseJson);

            FaceBodyResult.FaceBodyResultBuilder builder = FaceBodyResult.builder().success(false).image(image).resp(responseJson);
            if (CollectionUtils.isNotEmpty(response.getBody().getData().getElements())) {
                for (DetectLivingFaceResponseBody.DetectLivingFaceResponseBodyDataElements element : response.getBody().getData().getElements()) {
                    if (CollectionUtils.isNotEmpty(element.getResults())) {
                        for (DetectLivingFaceResponseBody.DetectLivingFaceResponseBodyDataElementsResults result : element.getResults()) {
                            String label = result.getLabel();
                            String suggestion = result.getSuggestion();
                            float rate = result.getRate() != null ? result.getRate() : 0;
                            if (("normal".equalsIgnoreCase(label) && "pass".equalsIgnoreCase(suggestion) && rate >= this.config.getNormalPassRate()) ||
                                ("normal".equalsIgnoreCase(label) && "review".equalsIgnoreCase(suggestion) && rate >= this.config.getNormalReviewRate()) ||
                                ("liveness".equalsIgnoreCase(label) && "review".equalsIgnoreCase(suggestion) && rate >= this.config.getReviewRate())) {
                                builder.success(true);
                                break;
                            }
                        }
                    }
                }
            }
            return builder.build();
        } catch (TeaException error) {
            log.error("AliyunFaceBodyService.detectFace failed.", error);
        } catch (Exception exception) {
            TeaException error = new TeaException(exception.getMessage(), exception);
            log.error("AliyunFaceBodyService.detectFace failed.", error);
        }
        return FaceBodyResult.builder().success(false).build();
    }

    @Override
    public FaceBodyResult compareFace(String target, String source) {
        try {
            CompareFaceAdvanceRequest request = new CompareFaceAdvanceRequest()
                .setImageURLAObject(new ByteArrayInputStream(Base64.decode(target)))
                .setImageURLBObject(new ByteArrayInputStream(Base64.decode(source)));

            CompareFaceResponse response = getClient().compareFaceAdvance(request, getRuntime());

            String responseJson = JacksonUtils.toJson(TeaModel.buildMap(response));
            log.info("AliyunFaceBodyService.compareFace response - {}.", responseJson);

            return FaceBodyResult.builder()
                .success(response.getBody().getData().getConfidence() > this.config.getConfidence())
                .source(source)
                .target(target)
                .resp(responseJson).build();
        } catch (TeaException error) {
            log.error("AliyunFaceBodyService.detectFace failed.", error);
        } catch (Exception exception) {
            TeaException error = new TeaException(exception.getMessage(), exception);
            log.error("AliyunFaceBodyService.detectFace failed.", error);
        }
        return FaceBodyResult.builder().success(false).build();
    }

    public com.aliyun.teautil.models.RuntimeOptions getRuntime() {
        return new com.aliyun.teautil.models.RuntimeOptions();
    }

    public Client getClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(this.config.getAccessKeyId())
            .setAccessKeySecret(this.config.getAccessKeySecret())
            .setEndpoint(this.config.getEndpoint());
        return new Client(config);
    }

    /**
     * @author elvea
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config implements Serializable {
        @Builder.Default
        private boolean enabled = false;
        @Builder.Default
        private String endpoint = "facebody.cn-shanghai.aliyuncs.com";
        @Builder.Default
        private String accessKeyId = "";
        @Builder.Default
        private String accessKeySecret = "";
        @Builder.Default
        private int normalPassRate = 45;
        @Builder.Default
        private int normalReviewRate = 60;
        @Builder.Default
        private int reviewRate = 75;
        @Builder.Default
        private int confidence = 60;
    }

}
