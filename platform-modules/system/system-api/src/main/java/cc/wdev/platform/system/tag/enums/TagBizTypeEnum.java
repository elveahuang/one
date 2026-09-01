package cc.wdev.platform.system.tag.enums;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标签类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum TagBizTypeEnum implements BaseBizTypeEnum {
    SYSTEM("SYSTEM", BizScopeTypeEnum.PLATFORM.getCode(), "系统标签"),
    USER("USER", BizScopeTypeEnum.PLATFORM.getCode(), "用户标签"),
    NONE("NONE", BizScopeTypeEnum.PLATFORM.getCode(), "未指定");

    private final String value;
    private final String scope;
    private final String description;

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.TAG_TYPE.getValue().toUpperCase();
    }

}
