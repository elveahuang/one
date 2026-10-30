package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.system.core.domain.entity.RoleEntity;
import cc.wdev.platform.system.core.domain.vo.UserIdentitySideVo;
import cc.wdev.platform.system.core.service.RoleService;
import cc.wdev.platform.system.core.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
 * 身份端切换的纯单元测试：不依赖数据库与 Redis。
 *
 * <p>身份被收敛为两端：管理端（平台管理员/系统管理员/普通用户，权限并集）与会员端（仅会员）。
 * 这里覆盖两端语义的边界，以及几个曾经导致真实缺陷的点：</p>
 * <ul>
 *   <li>服务端记录必须优先于请求头，否则伪造 header 可提权</li>
 *   <li>管理端内的三类角色都归入同一个端，不再各自可切</li>
 *   <li>只属于单一端的用户不出现切换入口</li>
 *   <li>端记录的有效期必须覆盖刷新令牌（14 天）</li>
 * </ul>
 *
 * @author elvea
 */
@ExtendWith(MockitoExtension.class)
class UserIdentitySideServiceImplTests {

    private static final long USER_ID = 1002001L;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private CacheService cacheService;

    private UserIdentitySideServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserIdentitySideServiceImpl(roleService, userRoleService, cacheService);
    }

    /**
     * 构造一个拥有指定角色类型的用户。
     */
    private void givenOwnedRoles(String... bizTypes) {
        Set<Long> roleIds = new LinkedHashSet<>();
        List<RoleEntity> roles = new ArrayList<>();
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
    @DisplayName("管理端三类角色归入同一个端，只显示两个可选项")
    void adminTypesCollapseIntoOneSide() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR", "USER", "MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        UserIdentitySideVo vo = service.getIdentitySide(USER_ID);

        assertEquals(List.of("ADMIN", "MEMBER"), vo.getOwned(), "管理端三类角色应折叠为一个 ADMIN");
        assertEquals(List.of("ADMIN", "MEMBER"), vo.getAvailable());
        assertTrue(vo.getSwitchable());
    }

    @Test
    @DisplayName("只有管理类角色时不可切换（只属于一个端）")
    void adminOnlyIsNotSwitchable() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR", "USER");
        when(cacheService.get(anyString())).thenReturn(null);

        UserIdentitySideVo vo = service.getIdentitySide(USER_ID);

        assertEquals(List.of("ADMIN"), vo.getOwned());
        assertEquals(1, vo.getAvailable().size());
        assertFalse(vo.getSwitchable());
    }

    @Test
    @DisplayName("纯会员只有会员端，不可切换")
    void memberOnlyIsNotSwitchable() {
        givenOwnedRoles("MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        UserIdentitySideVo vo = service.getIdentitySide(USER_ID);

        assertEquals(List.of("MEMBER"), vo.getOwned());
        assertFalse(vo.getSwitchable());
    }

    @Test
    @DisplayName("服务端记录优先于请求头，防止伪造 header 越端")
    void storedSideWinsOverRequestedHeader() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");
        // 用户已切到会员端
        when(cacheService.get(anyString())).thenReturn("MEMBER");

        // 请求头伪造为管理端
        String effective = service.resolveEffectiveIdentitySide(USER_ID, "ADMIN");

        assertEquals("MEMBER", effective, "已切到会员端的用户不能被请求头拉回管理端");
    }

    @Test
    @DisplayName("无服务端记录时才接受请求头")
    void requestedHeaderAcceptedOnlyWithoutStoredRecord() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        assertEquals("MEMBER", service.resolveEffectiveIdentitySide(USER_ID, "MEMBER"));
    }

    @Test
    @DisplayName("请求头指定的端不在本人归属内时被忽略")
    void requestedHeaderIgnoredWhenNotOwned() {
        givenOwnedRoles("MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        // 只有会员端，请求管理端应被忽略；单端用户不做过滤，返回 null
        assertNull(service.resolveEffectiveIdentitySide(USER_ID, "ADMIN"));
    }

    @Test
    @DisplayName("拥有两端时首次访问自动落到管理端")
    void firstAccessInitializesToAdmin() {
        givenOwnedRoles("SYSTEM_ADMINISTRATOR", "MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        String effective = service.resolveEffectiveIdentitySide(USER_ID, null);

        assertEquals("ADMIN", effective);
        verify(cacheService).set(anyString(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("身份端记录有效期覆盖刷新令牌（14 天）")
    void sideTtlCoversRefreshToken() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");
        when(cacheService.get(anyString())).thenReturn(null);

        service.resolveEffectiveIdentitySide(USER_ID, null);

        org.mockito.ArgumentCaptor<Duration> captor = org.mockito.ArgumentCaptor.forClass(Duration.class);
        verify(cacheService).set(anyString(), any(), captor.capture());
        assertTrue(captor.getValue().toDays() >= 14,
            "TTL 必须覆盖刷新令牌，否则身份会在会话中途过期并被静默提升，实际为 " + captor.getValue().toDays() + " 天");
    }

    @Test
    @DisplayName("切换到本人未拥有的端被拒绝")
    void switchToUnownedSideRejected() {
        givenOwnedRoles("MEMBER");

        assertThrows(ServiceException.class, () -> service.switchIdentitySide(USER_ID, "ADMIN"));
        verify(cacheService, never()).set(anyString(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("切换到无法识别的端被拒绝")
    void switchToUnknownSideRejected() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");

        assertThrows(ServiceException.class, () -> service.switchIdentitySide(USER_ID, "SUPERUSER"));
        assertThrows(ServiceException.class, () -> service.switchIdentitySide(USER_ID, "HACKER"));
    }

    @Test
    @DisplayName("切换到空值被拒绝")
    void switchToBlankRejected() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");

        assertThrows(ServiceException.class, () -> service.switchIdentitySide(USER_ID, "  "));
    }

    @Test
    @DisplayName("端名大小写不敏感")
    void switchIsCaseInsensitive() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");

        UserIdentitySideVo vo = service.switchIdentitySide(USER_ID, "member");

        assertEquals("MEMBER", vo.getCurrent());
    }

    @Test
    @DisplayName("用角色类型名切换不被接受（端才是切换单位）")
    void roleTypeNameIsNotAValidSide() {
        givenOwnedRoles("PLATFORM_ADMINISTRATOR", "MEMBER");

        assertThrows(ServiceException.class, () -> service.switchIdentitySide(USER_ID, "PLATFORM_ADMINISTRATOR"));
    }

    @Test
    @DisplayName("无角色的用户解析为不过滤")
    void userWithoutRolesResolvesToNull() {
        when(userRoleService.findRoleIdsByUserId(USER_ID)).thenReturn(Set.of());

        assertNull(service.resolveEffectiveIdentitySide(USER_ID, "ADMIN"));
        assertNull(service.resolveEffectiveIdentitySide(USER_ID, null));
    }

    @Test
    @DisplayName("非法用户ID返回空结果，不抛异常")
    void invalidUserIdIsSafe() {
        assertNull(service.resolveEffectiveIdentitySide(0L, "ADMIN"));
        assertNull(service.resolveEffectiveIdentitySide(null, "ADMIN"));
        service.clearIdentitySide(0L);
        verify(cacheService, never()).delete(any(String[].class));
    }

}
