package cc.wdev.platform.system.commons.strategy.impl;

import cc.wdev.platform.system.commons.enums.WeiXinEventTypeEnum;
import cc.wdev.platform.system.commons.strategy.WeiXinEventHandle;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

@Component
public class WeiXinUnSubscribeEventHandle extends WeiXinEventHandle {

    @Override
    protected WeiXinEventTypeEnum getMsgEventType() {
        return WeiXinEventTypeEnum.UNSUBSCRIBE;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage message) {
        return WxMpXmlOutMessage.TEXT().build();
    }
}
