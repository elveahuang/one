package cc.wdev.platform.commons.extensions.sensitive.mark;

import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * @author elvea
 */
@AllArgsConstructor
public enum SensitiveMarkStrategy {
    /**
     * 密钥
     */
    API_KEY(SensitiveUtils::apiKey),
    /**
     * 身份证
     */
    ID_CARD(SensitiveUtils::idCard),
    /**
     * 手机号码
     */
    MOBILE(SensitiveUtils::mobileNumber),
    /**
     * 电子邮箱
     */
    EMAIL(SensitiveUtils::email);

    private final Function<String, String> handler;

    public Function<String, String> handler() {
        return handler;
    }

}
