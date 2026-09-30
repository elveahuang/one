package cc.wdev.platform.commons.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author elvea
 */
public abstract class WebUtils {

    public static <T> Page<T> emptyPage(Pageable pageable) {
        return Page.empty(pageable);
    }

    public static <T> Page<T> newPage(Page<?> page, List<T> content) {
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    public static <T> Page<T> newPage(Pageable pageable, List<T> content, long total) {
        return new PageImpl<>(content, pageable, total);
    }

}
