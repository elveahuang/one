package cc.wdev.platform.commons.data.mybatis.utils;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.web.request.PageRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author elvea
 */
public abstract class MyBatisPlusUtils {

    /**
     * 把MyBatis-Plus的分页对象转换成为Spring-Data的分页对象
     */
    public static <T> org.springframework.data.domain.Page<T> toSpringDataPage(IPage<T> mybatisPlusPage) {
        return toSpringDataPage(mybatisPlusPage, mybatisPlusPage.getRecords());
    }

    /**
     * 把MyBatis-Plus的分页对象转换成为Spring-Data的分页对象
     */
    public static <T> org.springframework.data.domain.Page<T> toSpringDataPage(IPage<?> mybatisPlusPage, List<T> list) {
        // Spring Data Pageable 目前只支持 int 类型的 page 和 size
        int page = Math.toIntExact(mybatisPlusPage.getCurrent() - 1);
        int size = Math.toIntExact(mybatisPlusPage.getSize());
        List<Sort.Order> orderList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(mybatisPlusPage.orders())) {
            for (OrderItem item : mybatisPlusPage.orders()) {
                if (item.isAsc()) {
                    orderList.add(Sort.Order.asc(item.getColumn()));
                } else {
                    orderList.add(Sort.Order.desc(item.getColumn()));
                }
            }
        }
        // 转换分页对象
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, Sort.by(orderList));
        return SpringDataUtils.toSpringDataPage(pageable, list, mybatisPlusPage.getTotal());
    }

    /**
     * 把MyBatis-Plus的分页对象转换成为Spring-Data的分页对象
     */
    public static <T> org.springframework.data.domain.Page<T> toSpringDataPage(Pageable pageable, List<T> list, Long total) {
        return SpringDataUtils.toSpringDataPage(pageable, list, total);
    }

    /**
     * 排序列名仅允许字母/数字/下划线/点号（标识符），防止前端可控排序字段拼接进 ORDER BY
     */
    private static final Pattern SORT_COLUMN_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    private static String safeSortColumn(String column) {
        if (Strings.isNullOrEmpty(column) || !SORT_COLUMN_PATTERN.matcher(column).matches()) {
            return null;
        }
        return column;
    }

    /**
     * 把Spring-Data的分页请求对象转换成为MyBatis-Plus的分页对象
     */
    public static <E> Page<E> getMyBatisPlusPage(Pageable pageable) {
        Page<E> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        List<OrderItem> orders = pageable.getSort().get()
            .filter(e -> safeSortColumn(e.getProperty()) != null)
            .map((e) ->
                Sort.Direction.ASC.equals(e.getDirection()) ? OrderItem.asc(e.getProperty()) : OrderItem.desc(e.getProperty())
            ).toList();
        page.setOrders(orders);
        return page;
    }

    /**
     * 把Spring-Data的分页请求对象转换成为MyBatis-Plus的分页对象
     */
    public static <E> Page<E> getMyBatisPlusPage(PageRequest pageRequest) {
        Page<E> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        String sort = safeSortColumn(pageRequest.getSort());
        if (!Strings.isNullOrEmpty(sort)) {
            if ("desc".equalsIgnoreCase(pageRequest.getOrder())) {
                page.setOrders(Collections.singletonList(OrderItem.desc(sort)));
            } else {
                page.setOrders(Collections.singletonList(OrderItem.asc(sort)));
            }
        }
        return page;
    }

    /**
     * 获取一个仅查询一条记录的分页对象
     */
    public static <E> Page<E> getLimitPage() {
        return new Page<>(1, 1);
    }

    /**
     * 获取一个仅查询一条记录的分页对象
     */
    public static <E> Page<E> getLimitPage(int limit) {
        return new Page<>(1, limit);
    }

    /**
     * 判断分页对象是否有数据
     */
    public static <E> boolean isNotEmpty(IPage<E> page) {
        return page != null && CollectionUtils.isNotEmpty(page.getRecords());
    }

    /**
     * 判断分页对象是否有数据
     */
    public static <E> boolean isEmpty(IPage<E> page) {
        return page == null || CollectionUtils.isEmpty(page.getRecords());
    }

}
