package cc.wdev.platform.commons.utils;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

/**
 * @author elvea
 */
@Slf4j
public class GsonUtilsTests {

    @Test
    public void testCmi() throws Exception {
        ClassPathResource resource = new ClassPathResource("json/cmi.json");
        String json = resource.getContentAsString(StandardCharsets.UTF_8);
        JsonObject object = GsonUtils.parse(json, JsonObject.class);
        Assertions.assertNotNull(object);
    }

}
