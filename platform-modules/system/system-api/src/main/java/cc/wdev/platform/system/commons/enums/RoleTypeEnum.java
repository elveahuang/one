package cc.wdev.platform.system.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleTypeEnum implements BaseRoleTypeEnum {
    PLATFORM_ADMINISTRATOR("PLATFORM_ADMINISTRATOR", RoleGroupTypeEnum.PLATFORM.getValue(), RoleDataScopeTypeEnum.ALL.getValue(), BizScopeTypeEnum.PLATFORM.getCode(), "平台管理员"),
    SYSTEM_ADMINISTRATOR("SYSTEM_ADMINISTRATOR", RoleGroupTypeEnum.SYSTEM.getValue(), RoleDataScopeTypeEnum.ALL.getValue(), BizScopeTypeEnum.SYSTEM.getCode(), "系统管理员"),
    USER("USER", RoleGroupTypeEnum.SYSTEM.getValue(), RoleDataScopeTypeEnum.MY.getValue(), BizScopeTypeEnum.SYSTEM.getCode(), "普通用户"),
    MEMBER("MEMBER", RoleGroupTypeEnum.MEMBER.getValue(), RoleDataScopeTypeEnum.MY.getValue(), BizScopeTypeEnum.SYSTEM.getCode(), "会员");

    private final String value;
    private final String roleGroupType;
    private final String roleScopeType;
    private final String scope;
    private final String description;

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.ROLE_TYPE.getValue().toUpperCase();
    }

}
