package cc.wdev.platform.system.config.controller.exchange;

import cc.wdev.platform.system.config.api.ConfigApi;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
public class ConfigExchangeController {

    private final ConfigApi configApi;

    @GetMapping(EXCHANGE_PREFIX + "/config")
    public ConfigVo getConfig(@RequestParam(value = "key") String key) {
        return this.configApi.getConfig(key);
    }

    @GetMapping(EXCHANGE_PREFIX + "/config/get-as-string")
    public String getString(@RequestParam(value = "key") String key) {
        return this.getString(key, "");
    }

    @GetMapping(EXCHANGE_PREFIX + "/config/get-as-string-with-default-value")
    public String getString(@RequestParam(value = "key") String key,
                            @RequestParam(value = "defaultValue") String defaultValue) {
        return this.configApi.getString(key, defaultValue);
    }

    @GetMapping(EXCHANGE_PREFIX + "/config/get-as-boolean")
    public boolean getBoolean(@RequestParam(value = "key") String key) {
        return this.getBoolean(key, false);
    }

    @GetMapping(EXCHANGE_PREFIX + "/config/get-as-boolean-with-default-value")
    public boolean getBoolean(@RequestParam(value = "key") String key,
                              @RequestParam(value = "defaultValue") boolean defaultValue) {
        return this.configApi.getBoolean(key, defaultValue);
    }

}
