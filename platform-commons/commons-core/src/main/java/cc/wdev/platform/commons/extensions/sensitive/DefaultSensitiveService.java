package cc.wdev.platform.commons.extensions.sensitive;

import cc.wdev.platform.commons.utils.EncryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultSensitiveService implements SensitiveService {

    public static final String DEFAULT_AES_KEY = "1234567812345678";

    public DefaultSensitiveService() {
        this.config = SensitiveConfig.builder().aesKey(DEFAULT_AES_KEY).build();
    }

    private final SensitiveConfig config;

    @Override
    public String encrypt(String value) {
        return EncryptUtils.aesEncrypt(value, this.config.getAesKey());
    }

    @Override
    public String decrypt(String value) {
        return EncryptUtils.aesDecrypt(value, this.config.getAesKey());
    }

}
