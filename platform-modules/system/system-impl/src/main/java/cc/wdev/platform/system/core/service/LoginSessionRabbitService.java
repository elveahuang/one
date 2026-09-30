package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.message.rabbit.RabbitService;
import cc.wdev.platform.system.core.domain.dto.LoginSessionDto;

/**
 * @author elvea
 */
public interface LoginSessionRabbitService extends RabbitService<LoginSessionDto> {
}
