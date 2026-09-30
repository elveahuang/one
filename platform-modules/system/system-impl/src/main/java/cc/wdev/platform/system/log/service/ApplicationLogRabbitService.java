package cc.wdev.platform.system.log.service;

import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.commons.message.rabbit.RabbitService;

/**
 * @author elvea
 */
public interface ApplicationLogRabbitService extends RabbitService<ApplicationLogDto> {
}
