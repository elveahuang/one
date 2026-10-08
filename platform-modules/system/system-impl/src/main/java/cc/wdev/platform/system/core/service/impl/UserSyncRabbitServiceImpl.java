package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.commons.constants.SystemRabbitConstants;
import cc.wdev.platform.system.core.domain.dto.AccountDto;
import cc.wdev.platform.system.core.service.UserSyncRabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = SystemRabbitConstants.ACCOUNT_SYNC_QUEUE)
public class UserSyncRabbitServiceImpl extends AbstractRabbitService<AccountDto> implements UserSyncRabbitService {

    @Override
    public void execute(AccountDto message) {
    }

    @Override
    public String getRoutingKey() {
        return SystemRabbitConstants.ACCOUNT_SYNC_QUEUE;
    }

}
