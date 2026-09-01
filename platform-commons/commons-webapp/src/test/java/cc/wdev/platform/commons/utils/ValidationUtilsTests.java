package cc.wdev.platform.commons.utils;

import cc.wdev.dev.webapp.BaseTests;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author elvea
 */
@Slf4j
public class ValidationUtilsTests extends BaseTests {

    @Autowired
    ValidationUtils validationUtils;

    @Test
    public void test() {
        Assertions.assertNotNull(validationUtils);

        User user = User.builder().name(null).build();
        ValidationUtils.Result result = this.validationUtils.validate(user);
        log.info("hasError - {}", result.isHasErrors());
        for (var v : result.getErrorMessages()) {
            log.info("{} ", v);
        }
    }

    @Test
    public void baseValidatorTest() {
        User user = User.builder().name(null).build();
        var violations = ValidationUtils.getDefaultValidator().validate(user);
        for (var v : violations) {
            log.info("{}: {}", v.getPropertyPath(), v.getMessage());
        }
    }

    @Data
    @Builder
    public static class User implements Serializable {
        @NotNull
        private String name;
    }

}
