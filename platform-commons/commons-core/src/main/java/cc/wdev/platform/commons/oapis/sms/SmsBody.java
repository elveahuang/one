package cc.wdev.platform.commons.oapis.sms;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@Builder
public class SmsBody implements Serializable {
    private String mobileCountryCode;
    private String mobileNumber;
    private String template;
    @Builder.Default
    private Map<String, Object> params = Maps.newHashMap();
}
