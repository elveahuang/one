package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.commons.constants.SystemRabbitConstants;
import cc.wdev.platform.system.message.api.MessageApi;
import cc.wdev.platform.system.message.domain.dto.SendMessageAmqpDto;
import cc.wdev.platform.system.message.service.MessageRabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Slf4j
@Service
@RabbitListener(queues = SystemRabbitConstants.MESSAGE_QUEUE)
public class MessageRabbitServiceImpl extends AbstractRabbitService<SendMessageAmqpDto> implements MessageRabbitService {

    private MessageApi messageApi;

    @Override
    public void execute(SendMessageAmqpDto dto) throws Exception {
        this.messageApi.sendMessage(dto.getId());
    }

    @Override
    public String getRoutingKey() {
        return SystemRabbitConstants.MESSAGE_QUEUE;
    }

    @Lazy
    @Autowired
    public void setMessageApi(MessageApi messageApi) {
        this.messageApi = messageApi;
    }

}
