package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 角色分组类型
 * 1. 平台角色 -- 顶层租户开放
 * 2. 系统角色 -- 所有租户开放
 * 3. 会员角色 -- 所有租户开放
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum RoleGroupTypeEnum implements BaseEnum<String> {
    PLATFORM("PLATFORM", "平台角色"),
    SYSTEM("SYSTEM", "系统角色"),
    MEMBER("MEMBER", "会员角色");

    private final String value;
    private final String description;

    public String getLabelKey() {
        return ("label__role_group_type__" + getValue()).toLowerCase();
    }

    /**
     * 顶层租户才拥有平台范围的业务类型
     */
    public static List<SimpleOptionVo> getRoleGroupTypes() {
        List<RoleGroupTypeEnum> enumList = Lists.newArrayList(SYSTEM, MEMBER);
        if (TenantContext.isRootTenant()) {
            enumList.add(PLATFORM);
        }
        return enumList.stream().map(s -> SimpleOptionVo.builder()
            .title(s.getValue())
            .value(s.getValue())
            .label(s.getDescription())
            .labelKey(s.getLabelKey())
            .labelGroup(s.getLabelGroup())
            .build()
        ).toList();
    }

}
