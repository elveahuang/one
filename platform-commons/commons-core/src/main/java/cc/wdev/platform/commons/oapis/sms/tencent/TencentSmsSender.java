package cc.wdev.platform.commons.oapis.sms.tencent;

import cc.wdev.platform.commons.oapis.sms.SmsBody;
import cc.wdev.platform.commons.oapis.sms.SmsResult;
import cc.wdev.platform.commons.oapis.sms.SmsSender;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;

import static com.tencentcloudapi.common.AbstractModel.toJsonString;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class TencentSmsSender implements SmsSender<TencentSmsSender.Config, SmsResult> {

    public final static String ENDPOINT = "sms.tencentcloudapi.com";

    private Config config;

    @Override
    public SmsResult send(SmsBody body) throws Exception {
        return this.send(this.config, body);
    }

    @Override
    public SmsResult send(Config config, SmsBody body) throws Exception {
        SmsClient client = createClient(config, body);
        SendSmsRequest request = getRequest(config, body);
        SendSmsResponse response = client.SendSms(request);

        return SmsResult.builder()
            .success(true)
            .response(toJsonString(response))
            .data(response)
            .build();
    }

    @NonNull
    private static SmsClient createClient(Config config, SmsBody body) {
        Credential credential = new Credential(config.getSecretId(), config.getSecretKey());

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setConnTimeout(60);
        httpProfile.setEndpoint(config.getEndpoint());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);

        return new SmsClient(credential, config.getRegion(), clientProfile);
    }

    @NonNull
    private static SendSmsRequest getRequest(Config config, SmsBody body) {
        SendSmsRequest request = new SendSmsRequest();
        request.setSmsSdkAppId(config.getAppId());
        request.setSignName(config.getSignName());
        request.setTemplateId(config.getTemplate());
        request.setPhoneNumberSet(new String[]{body.getMobileNumber()});
        request.setTemplateParamSet(body.getParams().keySet().toArray(new String[0]));
        request.setSessionContext("");
        request.setExtendCode("");
        request.setSenderId("");
        return request;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config implements Serializable {
        @Builder.Default
        private Boolean enabled = Boolean.FALSE;
        @Builder.Default
        private String endpoint = ENDPOINT;
        private String region;
        private String appId;
        private String secretId;
        private String secretKey;
        private String signName;
        private String template;
    }

}
