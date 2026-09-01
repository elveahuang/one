package cc.wdev.platform.system.message.service;

import cc.wdev.platform.commons.message.rabbit.RabbitService;
import cc.wdev.platform.system.message.domain.dto.SendMessageAmqpDto;

/**
 * @author elvea
 */
public interface MessageRabbitService extends RabbitService<SendMessageAmqpDto> {
}
