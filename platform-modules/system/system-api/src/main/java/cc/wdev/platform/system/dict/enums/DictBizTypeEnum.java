package cc.wdev.platform.system.dict.enums;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型
 * BANNER - 宣传栏类型
 * ANNOUNCEMENT - 资讯类型
 * NOTICE - 通知类型
 * ACCOUNT - 账号标识
 * USER - 用户标识
 * DEPARTMENT - 部门标识
 * POSITION - 岗位标识
 * LINK - 友情链接
 * LINK_CATALOG - 友情链接分类
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum DictBizTypeEnum implements BaseBizTypeEnum {
    BANNER("BANNER", BizScopeTypeEnum.PLATFORM.getCode(), "宣传栏类型"),
    ANNOUNCEMENT("ANNOUNCEMENT", BizScopeTypeEnum.PLATFORM.getCode(), "资讯类型"),
    NOTICE("NOTICE", BizScopeTypeEnum.PLATFORM.getCode(), "通知类型"),
    ACCOUNT("ACCOUNT", BizScopeTypeEnum.PLATFORM.getCode(), "账号标识"),
    USER("USER", BizScopeTypeEnum.PLATFORM.getCode(), "用户标识"),
    DEPARTMENT("DEPARTMENT", BizScopeTypeEnum.PLATFORM.getCode(), "部门标识"),
    POSITION("POSITION", BizScopeTypeEnum.PLATFORM.getCode(), "岗位标识"),
    LINK("LINK", BizScopeTypeEnum.PLATFORM.getCode(), "友情链接"),
    LINK_CATALOG("LINK_CATALOG", BizScopeTypeEnum.PLATFORM.getCode(), "友情链接分类"),
    NONE("NONE", BizScopeTypeEnum.PLATFORM.getCode(), "NONE");

    private final String value;
    private final String scope;
    private final String description;

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.DICT_TYPE.getValue().toUpperCase();
    }

}
