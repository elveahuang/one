package cc.wdev.platform.commons.web.request;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageRequest extends Request {
    /**
     * 页码
     */
    @Builder.Default
    @Schema(title = "页码", defaultValue = "1", description = "当前页码")
    private int page = 1;
    /**
     * 记录数
     */
    @Builder.Default
    @Schema(title = "记录数", defaultValue = "10", description = "每页记录数")
    private int size = 10;
    /**
     * 排序字段
     */
    @Schema(title = "排序字段", example = "id", defaultValue = "id", description = "排序字段")
    private String sort;
    /**
     * 排序方式
     */
    @Schema(title = "排序方式", description = "排序方式", example = "asc")
    private String order;
    /**
     * 搜索关键字
     */
    @Schema(title = "搜索关键字", description = "搜索关键字")
    @Size(max = 120, message = "搜索关键字文本长度不能超过120个字符")
    private String q;

    /**
     * 获取分页对象
     */
    @Schema(title = "分页对象", description = "分页对象")
    public Pageable getPageable() {
        if (StringUtils.isNotEmpty(sort)) {
            Sort.Direction direction = Sort.Direction.fromOptionalString(this.getOrder()).orElse(Sort.Direction.ASC);
            return org.springframework.data.domain.PageRequest.of(this.page - 1, size, Sort.by(direction, sort));
        } else {
            return org.springframework.data.domain.PageRequest.of(this.page - 1, size);
        }
    }

    /**
     * 排序字段必须在指定的范围内，否则置为空
     */
    public boolean check(List<String> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            if (columns.stream().noneMatch((column -> StringUtils.equals(column, this.sort)))) {
                this.setSort(null);
                this.setOrder(null);
                return false;
            }
            return true;
        }
        return false;
    }

}
