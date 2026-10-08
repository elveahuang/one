package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.utils.builder.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
public class BuilderTests {

    @Test
    public void test() {
        User user = Builder.of(User::new)
            .with(User::setUsername, "mike")
            .build();
        Assertions.assertEquals("mike", user.getUsername());
    }

    @Getter
    @Setter
    static class User {

        private String username;

    }

}
