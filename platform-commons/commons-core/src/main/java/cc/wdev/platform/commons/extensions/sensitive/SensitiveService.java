package cc.wdev.platform.commons.extensions.sensitive;

/**
 * @author elvea
 */
public interface SensitiveService {

    String encrypt(String value);

    String decrypt(String value);

}
