package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaLogDto;
import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.log.domain.converter.CaptchaLogConverter;
import cc.wdev.platform.system.log.domain.entity.CaptchaLogEntity;
import cc.wdev.platform.system.log.service.CaptchaLogRabbitService;
import cc.wdev.platform.system.log.service.CaptchaLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static cc.wdev.platform.system.commons.constants.SystemRabbitConstants.CAPTCHA_LOG_QUEUE;

/**
 * @author elvea
 * @see CaptchaLogService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
@RabbitListener(queues = CAPTCHA_LOG_QUEUE)
public class CaptchaLogRabbitServiceImpl extends AbstractRabbitService<CaptchaLogDto> implements CaptchaLogRabbitService {

    private final CaptchaLogService captchaLogService;

    public CaptchaLogRabbitServiceImpl(CaptchaLogService captchaLogService) {
        this.captchaLogService = captchaLogService;
    }

    @Override
    public void execute(CaptchaLogDto dto) {
        CaptchaLogEntity entity = CaptchaLogConverter.INSTANCE.dto2Entity(dto);
        this.captchaLogService.save(entity);
    }

    @Override
    public String getRoutingKey() {
        return CAPTCHA_LOG_QUEUE;
    }

}
