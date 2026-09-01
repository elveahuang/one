package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.log.domain.converter.ApplicationLogConverter;
import cc.wdev.platform.system.log.domain.entity.ApplicationLogEntity;
import cc.wdev.platform.system.log.service.ApplicationLogRabbitService;
import cc.wdev.platform.system.log.service.ApplicationLogService;
import cc.wdev.platform.system.log.service.CaptchaLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static cc.wdev.platform.system.commons.constants.SystemRabbitConstants.APPLICATION_LOG_QUEUE;

/**
 * @author elvea
 * @see CaptchaLogService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
@RabbitListener(queues = APPLICATION_LOG_QUEUE)
public class ApplicationLogRabbitServiceImpl extends AbstractRabbitService<ApplicationLogDto> implements ApplicationLogRabbitService {

    private final ApplicationLogService applicationLogService;

    public ApplicationLogRabbitServiceImpl(ApplicationLogService applicationLogService) {
        this.applicationLogService = applicationLogService;
    }

    @Override
    public void execute(ApplicationLogDto dto) {
        ApplicationLogEntity entity = ApplicationLogConverter.INSTANCE.dto2Entity(dto);
        this.applicationLogService.save(entity);
    }

    @Override
    public String getRoutingKey() {
        return APPLICATION_LOG_QUEUE;
    }

}
