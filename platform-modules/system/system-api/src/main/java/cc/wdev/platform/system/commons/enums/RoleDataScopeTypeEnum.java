package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * 角色数据范围类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum RoleDataScopeTypeEnum implements BaseEnum<String> {
    ALL("ALL", 9999, "全部数据"),
    MY("MY", 1111, "个人数据");

    private final String value;
    private final int level;
    private final String description;

    public String getLabelKey() {
        return ("label__role_data_scope_type__" + getValue()).toLowerCase();
    }

    public static RoleDataScopeTypeEnum getDataScope() {
        return getDataScope(SecurityUtils.getDataScopes());
    }

    public static RoleDataScopeTypeEnum getDataScope(Collection<String> scopes) {
        if (CollectionUtils.isEmpty(scopes)) {
            return RoleDataScopeTypeEnum.MY;
        }

        return scopes.stream()
            .map(scope -> BaseEnum.getEnumByValue(scope, RoleDataScopeTypeEnum.class, RoleDataScopeTypeEnum.MY))
            .filter(Objects::nonNull)
            .max(Comparator.comparingInt(RoleDataScopeTypeEnum::getLevel))
            .orElse(RoleDataScopeTypeEnum.MY);
    }

    public static List<SimpleOptionVo> getRoleDataScopeTypes() {
        return Arrays.stream(RoleDataScopeTypeEnum.values()).map(s -> SimpleOptionVo.builder()
            .title(s.getValue())
            .value(s.getValue())
            .label(s.getDescription())
            .labelKey(s.getLabelKey())
            .labelGroup(s.getLabelGroup())
            .build()
        ).toList();
    }

}
