package cc.wdev.platform.system.commons.strategy;

import cc.wdev.platform.system.commons.enums.WeiXinEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

@Slf4j
public abstract class WeiXinEventHandle {

    protected abstract WeiXinEventTypeEnum getMsgEventType();

    public abstract WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage);

}
