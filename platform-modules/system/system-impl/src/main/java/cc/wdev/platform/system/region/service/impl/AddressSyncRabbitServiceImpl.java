package cc.wdev.platform.system.region.service.impl;

import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.commons.constants.SystemRabbitConstants;
import cc.wdev.platform.system.region.domain.dto.AddressDto;
import cc.wdev.platform.system.region.service.AddressSyncRabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = SystemRabbitConstants.ADDRESS_SYNC_QUEUE)
public class AddressSyncRabbitServiceImpl extends AbstractRabbitService<AddressDto> implements AddressSyncRabbitService {

    @Override
    public void execute(AddressDto message) {
    }

    @Override
    public String getRoutingKey() {
        return SystemRabbitConstants.ADDRESS_SYNC_QUEUE;
    }

}
