package cc.wdev.platform.system.message.base;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;

/**
 * @author elvea
 */
public interface BaseMessageChannelEnum extends BaseBizTypeEnum {

    String getTitle();

    String getTemplateType();

    Boolean getEnabled();

}
