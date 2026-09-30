package cc.wdev.platform.system.region.service;

import cc.wdev.platform.commons.message.rabbit.RabbitService;
import cc.wdev.platform.system.region.domain.dto.AddressDto;

public interface AddressSyncRabbitService extends RabbitService<AddressDto> {
}
