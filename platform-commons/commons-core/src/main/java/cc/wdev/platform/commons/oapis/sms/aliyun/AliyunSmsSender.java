package cc.wdev.platform.commons.oapis.sms.aliyun;

import cc.wdev.platform.commons.oapis.sms.SmsBody;
import cc.wdev.platform.commons.oapis.sms.SmsResult;
import cc.wdev.platform.commons.oapis.sms.SmsSender;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AliyunSmsSender implements SmsSender<AliyunSmsSender.Config, SmsResult> {

    public final static String ENDPOINT = "dysmsapi.aliyuncs.com";

    private Config config;

    @Override
    public SmsResult send(SmsBody body) throws Exception {
        return this.send(this.config, body);
    }

    @Override
    public SmsResult send(Config config, SmsBody body) throws Exception {
        Client client = createClient(config, body);
        SendSmsRequest request = getRequest(config, body);
        SendSmsResponse response = client.sendSmsWithOptions(request, new RuntimeOptions());

        return SmsResult.builder()
            .success(true)
            .response(GsonUtils.toJson(response.toMap()))
            .data(response)
            .build();
    }

    @NonNull
    private static Client createClient(Config config, SmsBody body) throws Exception {
        com.aliyun.teaopenapi.models.Config aliyunConfig = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(config.getAccessKeyId())
            .setAccessKeySecret(config.getAccessKeySecret());
        aliyunConfig.setEndpoint(config.getEndpoint());
        return new Client(aliyunConfig);
    }

    @NonNull
    private static SendSmsRequest getRequest(Config config, SmsBody body) {
        String template = StringUtils.isNotEmpty(body.getTemplate()) ? body.getTemplate() : config.getTemplate();

        SendSmsRequest request = new SendSmsRequest();
        String mobileNumber = nvl(body.getMobileNumber());
        if (StringUtils.isNotBlank(body.getMobileCountryCode())) {
            mobileNumber = "+" + nvl(body.getMobileCountryCode()) + mobileNumber;
        }
        request.setPhoneNumbers(mobileNumber);
        request.setSignName(config.getSignName());
        request.setTemplateCode(template);
        request.setTemplateParam(GsonUtils.toJson(body.getParams()));

        return request;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config implements Serializable {
        @Builder.Default
        private Boolean enabled = false;
        @Builder.Default
        private String endpoint = ENDPOINT;
        private String accessKeyId;
        private String accessKeySecret;
        private String signName;
        private String template;
    }

}
