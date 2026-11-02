package cc.wdev.platform.commons.data.mybatis.utils;

import cc.wdev.platform.commons.web.request.PageRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 分页排序安全校验单元测试
 *
 * <p>覆盖 2026-11 加固的两道防线：标识符正则（防 SQL 拼接）与敏感列黑名单（防排序侧信道探测）。</p>
 *
 * @author elvea
 */
class MyBatisPlusUtilsSortTests {

    private static Page<Object> pageOf(String sort, String order) {
        PageRequest request = new PageRequest();
        request.setPage(1);
        request.setSize(10);
        request.setSort(sort);
        request.setOrder(order);
        return MyBatisPlusUtils.getMyBatisPlusPage(request);
    }

    @Test
    void testNormalColumnIsOrdered() {
        Page<Object> page = pageOf("username", "desc");

        Assertions.assertEquals(1, page.orders().size());
        Assertions.assertEquals("username", page.orders().getFirst().getColumn());
        Assertions.assertFalse(page.orders().getFirst().isAsc());
    }

    @Test
    void testAscIsDefault() {
        Page<Object> page = pageOf("created_at", null);

        Assertions.assertEquals(1, page.orders().size());
        Assertions.assertTrue(page.orders().getFirst().isAsc());
    }

    /**
     * 敏感列即使字符合法也必须被丢弃，避免通过排序探测密码/密钥等字段
     */
    @Test
    void testSensitiveColumnsAreDropped() {
        for (String column : new String[]{"password", "PASSWORD", "client_secret", "access_token", "refresh_token", "api_key", "salt"}) {
            Page<Object> page = pageOf(column, "asc");
            Assertions.assertTrue(page.orders().isEmpty(), "敏感列未被拦截: " + column);
        }
    }

    /**
     * 非法标识符（注入尝试）必须被丢弃
     */
    @Test
    void testIllegalIdentifierIsDropped() {
        for (String column : new String[]{"id;DROP TABLE sys_user", "id--", "id'", "id)", "", "id desc,password"}) {
            Page<Object> page = pageOf(column, "asc");
            Assertions.assertTrue(page.orders().isEmpty(), "非法标识符未被拦截: " + column);
        }
    }

    @Test
    void testBlankSortHasNoOrders() {
        Assertions.assertTrue(pageOf(null, "asc").orders().isEmpty());
        Assertions.assertTrue(pageOf("", "asc").orders().isEmpty());
        Assertions.assertTrue(pageOf("   ", "asc").orders().isEmpty());
    }

    @Test
    void testPageNumberConversion() {
        PageRequest request = new PageRequest();
        request.setPage(3);
        request.setSize(20);

        Page<Object> page = MyBatisPlusUtils.getMyBatisPlusPage(request);
        // PageRequest 的 page 是 1 基，MyBatis-Plus 直接使用该值
        Assertions.assertEquals(3, page.getCurrent());
        Assertions.assertEquals(20, page.getSize());
    }

    @Test
    void testWhitelistCheckRejectsUnknownColumn() {
        PageRequest request = new PageRequest();
        request.setSort("password");
        request.setOrder("desc");

        boolean accepted = request.check(java.util.List.of("id", "username", "created_at"));

        Assertions.assertFalse(accepted);
        Assertions.assertNull(request.getSort());
        Assertions.assertNull(request.getOrder());
    }

    @Test
    void testWhitelistCheckAcceptsKnownColumn() {
        PageRequest request = new PageRequest();
        request.setSort("created_at");
        request.setOrder("desc");

        Assertions.assertTrue(request.check(java.util.List.of("id", "created_at")));
        Assertions.assertEquals("created_at", request.getSort());
    }

}
