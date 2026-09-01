package cc.wdev.platform.system.commons.domain;

import cc.wdev.platform.system.commons.enums.AuthorityTypeEnum;
import cc.wdev.platform.system.commons.enums.BaseRoleTypeEnum;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.PackageBizTypeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * @author elvea
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityNode implements Serializable {

    public static final AuthorityNode[] EMPTY_AUTHORITY_NODE = createNodes();

    protected String code;
    protected String title;
    protected PackageBizTypeEnum authorityBizType;
    protected AuthorityTypeEnum authorityType;
    protected BizScopeTypeEnum authorityScopeType;
    protected BaseRoleTypeEnum[] roleTypes;
    protected AuthorityNode[] items;
    protected Integer idx;
    protected Integer active;

    // -----------------------------------------------------------------------------------------------------------------
    // 权限模块
    // -----------------------------------------------------------------------------------------------------------------

    public static class Module extends AuthorityNode {
        public Module(String code, String title,
                      BizScopeTypeEnum authorityScopeType, BaseRoleTypeEnum[] roleTypes, AuthorityNode[] items,
                      int idx, int active) {
            super(code, title, PackageBizTypeEnum.TENANT, AuthorityTypeEnum.MODULE, authorityScopeType, roleTypes, items, idx, active);
        }
    }

    public static Module createSystemModule(String code, String title, AuthorityNode[] items, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Module(code, title, BizScopeTypeEnum.SYSTEM, roleTypes, items, idx, active);
    }

    public static Module createPlatformModule(String code, String title, AuthorityNode[] items, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Module(code, title, BizScopeTypeEnum.PLATFORM, roleTypes, items, idx, active);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 权限分组
    // -----------------------------------------------------------------------------------------------------------------

    public static class Group extends AuthorityNode {
        public Group(String code, String title,
                     BizScopeTypeEnum authorityScopeType, BaseRoleTypeEnum[] roleTypes, AuthorityNode[] items,
                     int idx, int active) {
            super(code, title, PackageBizTypeEnum.TENANT, AuthorityTypeEnum.GROUP, authorityScopeType, roleTypes, items, idx, active);
        }
    }

    public static Group createSystemGroup(String code, String title, AuthorityNode[] items, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Group(code, title, BizScopeTypeEnum.SYSTEM, roleTypes, items, idx, active);
    }

    public static Group createPlatformGroup(String code, String title, AuthorityNode[] items, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Group(code, title, BizScopeTypeEnum.PLATFORM, roleTypes, items, idx, active);
    }

    public static Group createPlatformGroup(String code, String title, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Group(code, title, BizScopeTypeEnum.PLATFORM, roleTypes, EMPTY_AUTHORITY_NODE, idx, active);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 权限分组
    // -----------------------------------------------------------------------------------------------------------------

    public static class Resource extends AuthorityNode {
        public Resource(String code, String title,
                        BizScopeTypeEnum authorityScopeType, BaseRoleTypeEnum[] roleTypes, AuthorityNode[] items,
                        int idx, int active) {
            super(code, title, PackageBizTypeEnum.TENANT, AuthorityTypeEnum.RESOURCE, authorityScopeType, roleTypes, items, idx, active);
        }
    }

    public static Resource createSystemResource(String code, String title, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Resource(code, title, BizScopeTypeEnum.SYSTEM, roleTypes, createNodes(), idx, active);
    }

    public static Resource createSystemResource(String code, String title, AuthorityNode[] items, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Resource(code, title, BizScopeTypeEnum.SYSTEM, roleTypes, items, idx, active);
    }

    public static Resource createPlatformResource(String code, String title, AuthorityNode[] items, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Resource(code, title, BizScopeTypeEnum.PLATFORM, roleTypes, items, idx, active);
    }

    public static Resource createPlatformResource(String code, String title, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Resource(code, title, BizScopeTypeEnum.PLATFORM, roleTypes, EMPTY_AUTHORITY_NODE, idx, active);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 权限操作
    // -----------------------------------------------------------------------------------------------------------------

    public static class Permission extends AuthorityNode {
        public Permission(String code, String title,
                          BizScopeTypeEnum authorityScopeType, BaseRoleTypeEnum[] roleTypes,
                          int idx, int active) {
            super(code, title, PackageBizTypeEnum.TENANT, AuthorityTypeEnum.PERMISSION, authorityScopeType, roleTypes, new AuthorityNode[]{}, idx, active);
        }
    }

    public static Permission createSystemPermission(String code, String title, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Permission(code, title, BizScopeTypeEnum.SYSTEM, roleTypes, idx, active);
    }

    public static Permission createPlatformPermission(String code, String title, BaseRoleTypeEnum[] roleTypes, int idx, int active) {
        return new AuthorityNode.Permission(code, title, BizScopeTypeEnum.PLATFORM, roleTypes, idx, active);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // 工具方法
    // -----------------------------------------------------------------------------------------------------------------

    public static AuthorityNode[] createNodes(AuthorityNode... nodes) {
        return nodes;
    }

    public static BaseRoleTypeEnum[] createRoleTypes(BaseRoleTypeEnum... roleTypes) {
        return roleTypes;
    }

}
