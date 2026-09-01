package cc.wdev.platform.commons.data.core.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author elvea
 */
public abstract class SpringDataUtils {

    public static <T> boolean isNotEmpty(Page<T> page) {
        return null != page && page.hasContent();
    }

    public static <T> boolean isEmpty(Page<T> page) {
        return null == page || page.isEmpty();
    }

    public static <T> Page<T> toSpringDataPage(Page<?> page, List<T> list) {
        return new PageImpl<>(list, page.getPageable(), page.getTotalElements());
    }

    public static <T> Page<T> toSpringDataPage(Pageable pageable, List<T> list, Long total) {
        return new PageImpl<>(list, pageable, total);
    }

    public static <T> Page<T> emptyPage() {
        return Page.empty();
    }

    public static <T> Page<T> emptyPage(Pageable pageable) {
        return Page.empty(pageable);
    }

}
