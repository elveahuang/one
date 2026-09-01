package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.commons.constants.SystemRabbitConstants;
import cc.wdev.platform.system.log.domain.converter.OperationLogConverter;
import cc.wdev.platform.system.log.domain.entity.OperationLogEntity;
import cc.wdev.platform.system.log.service.OperationLogRabbitService;
import cc.wdev.platform.system.log.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 * @see OperationLogService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
@RabbitListener(queues = SystemRabbitConstants.OPERATION_LOG_QUEUE)
public class OperationLogRabbitServiceImpl extends AbstractRabbitService<OperationLogDto> implements OperationLogRabbitService {

    private final OperationLogService operationLogService;

    public OperationLogRabbitServiceImpl(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Override
    public void execute(OperationLogDto dto) {
        OperationLogEntity entity = OperationLogConverter.INSTANCE.dto2Entity(dto);
        this.operationLogService.save(entity);
    }

    @Override
    public String getRoutingKey() {
        return SystemRabbitConstants.OPERATION_LOG_QUEUE;
    }

}
