package cc.wdev.platform.system.message.controller.exchange;

import cc.wdev.platform.system.message.api.MessageApi;
import cc.wdev.platform.system.message.domain.dto.CreateMessageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

@Slf4j
@RestController
@AllArgsConstructor
public class MessageExchangeController {

    private final MessageApi messageApi;

    @PostMapping(EXCHANGE_PREFIX + "/message/create")
    public Long createMessage(@RequestBody CreateMessageDto message) throws Exception {
        return messageApi.createMessage(message);
    }

}
