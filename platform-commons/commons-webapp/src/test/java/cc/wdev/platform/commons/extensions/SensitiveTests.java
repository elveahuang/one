package cc.wdev.platform.commons.extensions;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.annotations.SensitiveEncrypt;
import cc.wdev.platform.commons.annotations.SensitiveMark;
import cc.wdev.platform.commons.extensions.sensitive.encrypt.SensitiveEncryptModule;
import cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkModule;
import cc.wdev.platform.commons.utils.jackson.CommonModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.Serializable;

import static cc.wdev.platform.commons.extensions.sensitive.encrypt.SensitiveEncryptStrategy.AES;
import static cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkStrategy.ID_CARD;
import static cc.wdev.platform.commons.extensions.sensitive.mark.SensitiveMarkStrategy.MOBILE;

/**
 * @author elvea
 */
@Slf4j
public class SensitiveTests extends BaseTests {

    @Test
    public void encryptTest() {
        JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new CommonModule())
            .addModule(new SensitiveEncryptModule())
            .build();

        String key = "1234567890";

        App app = App.builder().key(key).build();
        String json = objectMapper.writeValueAsString(app);
        Assertions.assertNotNull(json);

        App decryptedApp = objectMapper.readValue(json, App.class);
        Assertions.assertNotNull(decryptedApp);
        Assertions.assertEquals(key, decryptedApp.getKey());
    }

    @Test
    public void markTest() {
        JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new CommonModule())
            .addModule(new SensitiveMarkModule())
            .build();

        User user = User.builder().id(1L).idCard("445121200001011234").mobileNumber("13800138000").build();
        String json = objectMapper.writeValueAsString(user);
        Assertions.assertNotNull(json);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class App implements Serializable {
        @SensitiveEncrypt(strategy = AES)
        private String key;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User implements Serializable {
        private Long id;
        @SensitiveMark(strategy = ID_CARD)
        private String idCard;
        @SensitiveMark(strategy = MOBILE)
        private String mobileNumber;
    }

}
