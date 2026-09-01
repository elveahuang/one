package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;

/**
 * @author elvea
 */
@Slf4j
public class EncryptUtilsTests {

    @Test
    public void secretKeyTest() {
        String key = EncryptUtils.generateKey(16);
        SecretKey secretKey = EncryptUtils.generateSecretKey(key);
        String text = EncryptUtils.toSecretKeyString(secretKey);
        SecretKey k = EncryptUtils.toSecretKey(text);
        Assertions.assertNotNull(text);
        Assertions.assertNotNull(k);
    }

    @Test
    public void keyPairTest() {
        KeyPair keyPair = EncryptUtils.generateKeyPair();
        log.info(EncryptUtils.toPublicKeyString(keyPair));
        log.info(EncryptUtils.toPrivateKeyString(keyPair));
    }

    @Test
    public void base64Test() {
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.encodeBase64(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.decodeBase64AsString(result));
    }

    @Test
    public void aesTest() {
        String key = EncryptUtils.generateKey(16);
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.aesEncrypt(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.aesDecrypt(result));

        result = EncryptUtils.aesEncrypt(text, key);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.aesDecrypt(result, key));
    }

    @Test
    public void desTest() {
        String key = EncryptUtils.generateKey(12);
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.desEncrypt(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.desDecrypt(result));

        result = EncryptUtils.desEncrypt(text, key);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.desDecrypt(result, key));
    }

    @Test
    public void md5Test() {
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.md5(text);
        Assertions.assertNotNull(result);
    }

    @Test
    public void shaTest() {
        String text = "~!@#$%^&*()_";
        byte[] result = EncryptUtils.sha(text);
        Assertions.assertNotNull(result);
    }

    @Test
    public void hexTest() {
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.encodeHex(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.decodeHexAsString(result));
    }

    @Test
    public void urlEncodeTest() {
        String text = "http://localhost:8080/test?id=1";
        String result = EncryptUtils.urlEncode(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.urlDecode(result));
    }

    @Test
    public void baseTest() {
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.encrypt(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.decrypt(result));
    }

    @Test
    public void escapeHtmlTest() {
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.escapeHtml(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.unescapeHtml(result));
    }

    @Test
    public void escapeXmlTest() {
        String text = "~!@#$%^&*()_";
        String result = EncryptUtils.escapeXml(text);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(text, EncryptUtils.unescapeXml(result));
    }

}
