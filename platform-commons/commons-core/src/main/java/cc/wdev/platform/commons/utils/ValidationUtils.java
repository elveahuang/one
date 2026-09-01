package cc.wdev.platform.commons.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
public record ValidationUtils(Validator validator) {

    /**
     * 初始一个默认的验证器，不依赖Spring上下文
     */
    public static Validator getDefaultValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    public Result validate(Object obj) {
        Result result = new Result();
        var violations = validator.validate(obj);
        for (var v : violations) {
            result.addError(v.getPropertyPath() + ": " + v.getMessage());
        }
        return result;
    }

    @Data
    public static class Result implements Serializable {
        private boolean hasErrors = false;
        private List<String> errorMessages = new ArrayList<>();

        public void addError(String message) {
            this.hasErrors = true;
            this.errorMessages.add(message);
        }
    }

}
