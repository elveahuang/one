package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.system.commons.enums.RoleTypeEnum;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.vo.UserRoleTypeVo;
import cc.wdev.platform.system.core.service.RoleService;
import cc.wdev.platform.system.core.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 身份切换的纯单元测试：不依赖数据库与 Redis。
 *
 * <p>覆盖几个曾经导致真实缺陷的边界：</p>
 * <ul>
 *   <li>服务端记录必须优先于请求头，否则伪造 header 可提权</li>
 *   <li>MEMBER 不得出现在可切换列表</li>
 *   <li>多身份用户首次访问要落到最高身份，保证展示与生效一致</li>
 *   <li>身份记录的有效期必须覆盖刷新令牌（14 天）</li>
 * </ul>
 *
 * @author elvea
 */
@ExtendWith(MockitoExtension.class)
class UserRoleTypeServiceImplTests {

    private static final long USER_ID = 1002001L;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private CacheService cacheService;

    private UserRoleTypeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserRoleTypeServiceImpl(roleService, userRoleService, cacheService);
    }

    /**
     * 构造一个拥有指定角色类型的用户。
     */
    private void givenOwnedTypes(String... bizTypes) {
        Set<Long> roleIds = new java.util.LinkedHashSet<>();
        List<RoleEntity> roles = new java.util.ArrayList<>();
        for (int i = 0; i < bizTypes.length; i++) {
            long roleId = i + 1L;
            roleIds.add(roleId);
            RoleEntity role = new RoleEntity();
            role.setId(roleId);
            role.setBizType(bizTypes[i]);
            roles.add(role);
        }
        when(userRoleService.findRoleIdsByUserId(USER_ID)).thenReturn(roleIds);
        when(roleService.findCacheByIds(any())).thenReturn(roles);
    }

    @Test
    @DisplayName("可切换列表排除 MEMBER")
    void availableExcludesMember() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR", "USER", "MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        UserRoleTypeVo vo = service.getRoleType(USER_ID);

        assertEquals(List.of("PLATFORM_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR", "USER"), vo.getAvailable());
        assertFalse(vo.getAvailable().contains(RoleTypeEnum.MEMBER.getValue()));
        assertTrue(vo.getOwned().contains(RoleTypeEnum.MEMBER.getValue()));
        assertTrue(vo.getSwitchable());
    }

    @Test
    @DisplayName("纯会员账号不可切换")
    void memberOnlyIsNotSwitchable() {
        givenOwnedTypes("MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        UserRoleTypeVo vo = service.getRoleType(USER_ID);

        assertTrue(vo.getAvailable().isEmpty());
        assertFalse(vo.getSwitchable());
        assertNull(vo.getCurrent());
    }

    @Test
    @DisplayName("单身份账号不可切换（切了没有意义）")
    void singleIdentityIsNotSwitchable() {
        givenOwnedTypes("SYSTEM_ADMINISTRATOR");
        when(cacheService.get(anyString())).thenReturn(null);

        UserRoleTypeVo vo = service.getRoleType(USER_ID);

        assertEquals(1, vo.getAvailable().size());
        assertFalse(vo.getSwitchable());
    }

    @Test
    @DisplayName("服务端记录优先于请求头，防止伪造 header 提权")
    void storedRoleTypeWinsOverRequestedHeader() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "USER");
        // 用户已切到 USER
        when(cacheService.get(anyString())).thenReturn("USER");

        // 请求头伪造为高权限
        String effective = service.resolveEffectiveRoleType(USER_ID, "PLATFORM_ADMINISTRATOR");

        assertEquals("USER", effective, "已切换的用户不能被请求头提回高权限");
    }

    @Test
    @DisplayName("无服务端记录时才接受请求头")
    void requestedHeaderAcceptedOnlyWithoutStoredRecord() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "USER");
        when(cacheService.get(anyString())).thenReturn(null);

        String effective = service.resolveEffectiveRoleType(USER_ID, "USER");

        assertEquals("USER", effective);
    }

    @Test
    @DisplayName("请求头指定的类型不在本人角色内时被忽略")
    void requestedHeaderIgnoredWhenNotOwned() {
        givenOwnedTypes("USER");
        when(cacheService.get(anyString())).thenReturn(null);

        String effective = service.resolveEffectiveRoleType(USER_ID, "PLATFORM_ADMINISTRATOR");

        // 单身份用户不做类型过滤，返回 null 表示按全部角色合并
        assertNull(effective);
    }

    @Test
    @DisplayName("首次访问自动落到最高身份，展示与生效一致")
    void firstAccessInitializesToHighestIdentity() {
        givenOwnedTypes("SYSTEM_ADMINISTRATOR", "USER", "MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        String effective = service.resolveEffectiveRoleType(USER_ID, null);

        assertEquals("SYSTEM_ADMINISTRATOR", effective, "SYSTEM 优先级高于 USER");
        verify(cacheService).set(anyString(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("身份记录有效期覆盖刷新令牌（14 天）")
    void roleTypeTtlCoversRefreshToken() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "USER");
        when(cacheService.get(anyString())).thenReturn(null);

        service.resolveEffectiveRoleType(USER_ID, null);

        org.mockito.ArgumentCaptor<Duration> captor = org.mockito.ArgumentCaptor.forClass(Duration.class);
        verify(cacheService).set(anyString(), any(), captor.capture());
        assertTrue(captor.getValue().toDays() >= 14,
            "TTL 必须覆盖刷新令牌，否则身份会在会话中途过期并被静默提升，实际为 " + captor.getValue().toDays() + " 天");
    }

    @Test
    @DisplayName("切换到本人未拥有的类型被拒绝")
    void switchToUnownedTypeRejected() {
        givenOwnedTypes("USER");

        assertThrows(ServiceException.class, () -> service.switchRoleType(USER_ID, "PLATFORM_ADMINISTRATOR"));
        verify(cacheService, never()).set(anyString(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("切换到 MEMBER 被拒绝（不在可切换列表）")
    void switchToMemberRejected() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "USER", "MEMBER");

        assertThrows(ServiceException.class, () -> service.switchRoleType(USER_ID, "MEMBER"));
    }

    @Test
    @DisplayName("切换到空值被拒绝")
    void switchToBlankRejected() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "USER");

        assertThrows(ServiceException.class, () -> service.switchRoleType(USER_ID, "  "));
    }

    @Test
    @DisplayName("切换时大小写不敏感")
    void switchIsCaseInsensitive() {
        givenOwnedTypes("PLATFORM_ADMINISTRATOR", "USER");

        UserRoleTypeVo vo = service.switchRoleType(USER_ID, "user");

        assertEquals("USER", vo.getCurrent());
    }

    @Test
    @DisplayName("无角色的用户解析结果为不过滤")
    void userWithoutRolesResolvesToNull() {
        when(userRoleService.findRoleIdsByUserId(USER_ID)).thenReturn(Set.of());

        assertNull(service.resolveEffectiveRoleType(USER_ID, "USER"));
        assertNull(service.resolveEffectiveRoleType(USER_ID, null));
    }

    @Test
    @DisplayName("非法用户ID返回空结果，不抛异常")
    void invalidUserIdIsSafe() {
        assertNull(service.resolveEffectiveRoleType(0L, "USER"));
        assertNull(service.resolveEffectiveRoleType(null, "USER"));
        service.clearRoleType(0L);
        verify(cacheService, never()).delete(any(String[].class));
    }

}
