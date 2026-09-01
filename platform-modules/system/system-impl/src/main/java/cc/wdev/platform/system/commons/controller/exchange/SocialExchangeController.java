package cc.wdev.platform.system.commons.controller.exchange;

import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.system.commons.api.SocialApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@RestController
@RequestMapping(EXCHANGE_PREFIX + "/social")
@RequiredArgsConstructor
public class SocialExchangeController {

    private final SocialApi socialApi;

    @PostMapping("/auth")
    public SocialUser retrieveSocialUser(@RequestParam("type") String type,
                                         @RequestBody Map<String, Object> parameters) {
        return this.socialApi.retrieveSocialUser(type, parameters);
    }

}
