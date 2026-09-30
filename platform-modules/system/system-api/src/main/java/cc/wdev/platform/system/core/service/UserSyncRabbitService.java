package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.message.rabbit.RabbitService;
import cc.wdev.platform.system.core.domain.dto.AccountDto;

/**
 * @author elvea
 */
public interface UserSyncRabbitService extends RabbitService<AccountDto> {
}
