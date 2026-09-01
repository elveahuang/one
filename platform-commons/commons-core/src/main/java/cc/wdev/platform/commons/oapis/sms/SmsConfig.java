package cc.wdev.platform.commons.oapis.sms;

import cc.wdev.platform.commons.oapis.sms.aliyun.AliyunSmsSender;
import cc.wdev.platform.commons.oapis.sms.enums.SmsTypeEnum;
import cc.wdev.platform.commons.oapis.sms.tencent.TencentSmsSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsConfig implements Serializable {

    @Builder.Default
    private SmsTypeEnum type = SmsTypeEnum.Aliyun;

    @Builder.Default
    private AliyunSmsSender.Config aliyun = AliyunSmsSender.Config.builder().build();

    @Builder.Default
    private TencentSmsSender.Config tencent = TencentSmsSender.Config.builder().build();

}
