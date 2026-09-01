package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.message.model.SimpleJsonMessage;
import cc.wdev.platform.commons.message.model.SimpleMessage;
import cc.wdev.platform.commons.message.model.SimpleTextMessage;
import cc.wdev.platform.commons.utils.jackson.CommonModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.io.Serializable;
import java.util.List;

import static tools.jackson.databind.DefaultTyping.NON_FINAL;

/**
 * @author elvea
 */
@Slf4j
public class JacksonUtilsTests {

    @Test
    public void baseTest() throws Exception {
        SimpleTextMessage simpleTextMessage = new SimpleTextMessage();
        simpleTextMessage.setContent("hello world");
        String simpleTextJson = JacksonUtils.toJson(simpleTextMessage);
        Assertions.assertNotNull(simpleTextJson);

        SimpleJsonMessage<JobChatTextMessage> seekMessage = new SimpleJsonMessage<>();
        seekMessage.setReceivers(List.of(1L));
        seekMessage.setContent(JobChatTextMessage.builder().content("elvea").build());
        String json = JacksonUtils.toJson(seekMessage);
        Assertions.assertNotNull(json);

        SimpleMessage<?> message = JacksonUtils.toObject(json, SimpleMessage.class);
        Assertions.assertNotNull(message);
    }

    @Test
    public void testCovert() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("cc.wdev")
            .build();

        JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new CommonModule())
            .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_MISSING_VALUES)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .activateDefaultTyping(ptv, NON_FINAL)
            .build();

        User user = User.builder().name("elvea").build();
        String json = objectMapper.writeValueAsString(user);
        log.info(json);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobChatTextMessage implements Serializable {
        private String content;
    }

    @Data
    @Builder
    public static class User implements Serializable {
        private String name;
    }

}
