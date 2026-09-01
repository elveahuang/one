package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BaseEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 业务范围类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum BizScopeTypeEnum implements BaseEnum<Integer> {
    PLATFORM(1, "PLATFORM", "平台范围，只允许平台使用，即顶层租户专属，不允许下放到其他租户"),
    SYSTEM(2, "SYSTEM", "系统范围，直接在页面添加的记录，一般情况下允许直接删除"),
    USER(3, "USER", "用户范围，直接在页面添加的记录，一般情况下允许直接删除"),
    MEMBER(4, "MEMBER", "会员范围，直接在页面添加的记录，一般情况下允许直接删除"),
    NONE(0, "NONE", "无范围");

    private final Integer value;
    private final String code;
    private final String description;

    public static boolean isPlatformScope(String code) {
        return BizScopeTypeEnum.PLATFORM.getCode().equalsIgnoreCase(code);
    }

    public static boolean isSystemScope(String code) {
        return BizScopeTypeEnum.SYSTEM.getCode().equalsIgnoreCase(code);
    }

    /**
     * 顶层租户才拥有平台范围的业务类型
     */
    public static List<String> getBizScopeTypes() {
        if (TenantContext.isRootTenant()) {
            return Lists.newArrayList(PLATFORM.getCode(), SYSTEM.getCode(), USER.getCode(), MEMBER.getCode());
        }
        return Lists.newArrayList(SYSTEM.getCode(), USER.getCode(), MEMBER.getCode());
    }

}
