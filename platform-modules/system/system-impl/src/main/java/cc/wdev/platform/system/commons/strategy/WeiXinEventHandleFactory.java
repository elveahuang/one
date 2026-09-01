package cc.wdev.platform.system.commons.strategy;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.system.commons.enums.WeiXinEventTypeEnum;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WeiXinEventHandleFactory implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static final Map<WeiXinEventTypeEnum, WeiXinEventHandle> OPERATE_STRATEGIES = Maps.newConcurrentMap();

    public static WeiXinEventHandle getHandle(WeiXinEventTypeEnum eventTypeEnum) {
        if (eventTypeEnum == null) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        if (!OPERATE_STRATEGIES.containsKey(eventTypeEnum)) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }
        return OPERATE_STRATEGIES.get(eventTypeEnum);
    }

    @Override
    public void afterPropertiesSet() {
        applicationContext.getBeansOfType(WeiXinEventHandle.class)
            .values()
            .forEach(eventHandle -> OPERATE_STRATEGIES.put(eventHandle.getMsgEventType(), eventHandle));
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
