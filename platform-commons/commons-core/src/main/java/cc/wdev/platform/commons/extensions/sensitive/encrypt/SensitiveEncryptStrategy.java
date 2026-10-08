package cc.wdev.platform.commons.extensions.sensitive.encrypt;

import cc.wdev.platform.commons.extensions.sensitive.SensitiveUtils;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * @author elvea
 */
@AllArgsConstructor
public enum SensitiveEncryptStrategy {
    AES(SensitiveUtils::doAesEncrypt, SensitiveUtils::doAesDecrypt);

    private final Function<String, String> encryptHandler;
    private final Function<String, String> decryptHandler;

    public Function<String, String> encryptHandler() {
        return encryptHandler;
    }

    public Function<String, String> decryptHandler() {
        return decryptHandler;
    }

}
