package cc.wdev.platform.system.core.controller.exchange;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.core.api.LoginSessionApi;
import cc.wdev.platform.system.core.domain.dto.LoginSessionDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author erden
 */
@Slf4j
@RestController
@RequestMapping(value = EXCHANGE_PREFIX + "/login-session")
@AllArgsConstructor
public class LoginSessionExchangeController {

    private final LoginSessionApi loginSessionApi;

    @PostMapping("/save-login-session")
    public R<Boolean> saveLoginSession(@RequestBody LoginSessionDto loginSession) throws Exception {
        return loginSessionApi.saveLoginSession(loginSession);
    }

}
