package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.enums.ActionTypeEnum;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.message.rabbit.AbstractRabbitService;
import cc.wdev.platform.system.commons.constants.SystemRabbitConstants;
import cc.wdev.platform.system.core.domain.converter.LoginSessionConverter;
import cc.wdev.platform.system.core.domain.dto.LoginSessionDto;
import cc.wdev.platform.system.core.domain.entity.LoginSessionEntity;
import cc.wdev.platform.system.core.service.LoginSessionRabbitService;
import cc.wdev.platform.system.core.service.LoginSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Slf4j
@Service
@RabbitListener(queues = SystemRabbitConstants.LOGIN_SESSION)
public class LoginSessionRabbitServiceImpl extends AbstractRabbitService<LoginSessionDto> implements LoginSessionRabbitService {

    private final LoginSessionService loginSessionService;

    public LoginSessionRabbitServiceImpl(LoginSessionService loginSessionService) {
        this.loginSessionService = loginSessionService;
    }

    @Override
    public void execute(LoginSessionDto dto) {
        LocalDateTime localDateTime = this.getCurLocalDateTime();
        LoginSessionEntity entity = this.loginSessionService.findBySessionId(dto.getSessionId());
        if (ActionTypeEnum.DELETE.equals(BaseEnum.getEnumByValue(dto.getActionType(), ActionTypeEnum.class))) {
            entity.setUa(dto.getUa());
            entity.setHost(dto.getHost());
            entity.setEndDatetime(localDateTime);
            entity.setUpdatedBy(dto.getUserId());
            entity.setUpdatedAt(localDateTime);
            entity.setDeletedBy(dto.getUserId());
            entity.setDeletedAt(localDateTime);
            this.loginSessionService.save(entity);
        } else if (entity != null) {
            entity.setUa(dto.getUa());
            entity.setHost(dto.getHost());
            entity.setLastAccessDatetime(localDateTime);
            entity.setLastAccessYear(localDateTime.getYear());
            entity.setLastAccessMonth(localDateTime.getMonthValue());
            entity.setLastAccessDay(localDateTime.getDayOfMonth());
            entity.setLastAccessHour(localDateTime.getHour());
            entity.setLastAccessMinute(localDateTime.getMinute());
            entity.setUpdatedBy(dto.getUserId());
            entity.setUpdatedAt(localDateTime);
            this.loginSessionService.save(entity);
        } else {
            entity = LoginSessionConverter.INSTANCE.dto2Entity(dto);
            entity.setStartDatetime(localDateTime);
            entity.setStartYear(localDateTime.getYear());
            entity.setStartMonth(localDateTime.getMonthValue());
            entity.setStartDay(localDateTime.getDayOfMonth());
            entity.setStartHour(localDateTime.getHour());
            entity.setStartMinute(localDateTime.getMinute());
            entity.setLastAccessDatetime(localDateTime);
            entity.setLastAccessYear(localDateTime.getYear());
            entity.setLastAccessMonth(localDateTime.getMonthValue());
            entity.setLastAccessDay(localDateTime.getDayOfMonth());
            entity.setLastAccessHour(localDateTime.getHour());
            entity.setLastAccessMinute(localDateTime.getMinute());
            entity.setCreatedBy(dto.getUserId());
            entity.setCreatedAt(localDateTime);
            entity.setUpdatedBy(dto.getUserId());
            entity.setUpdatedAt(localDateTime);
            this.loginSessionService.save(entity);
        }

    }

    @Override
    public String getRoutingKey() {
        return SystemRabbitConstants.LOGIN_SESSION;
    }

}
