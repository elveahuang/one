package cc.wdev.platform.commons.core;

import cc.wdev.dev.webapp.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author elvea
 */
@Slf4j
public class CoreTests extends BaseTests {

    @Test
    public void test() {
        File root = new File(".");
        System.out.printf("root: %s%n", root.getAbsolutePath());

        System.out.println(StandardCharsets.UTF_8.displayName());
    }

}
