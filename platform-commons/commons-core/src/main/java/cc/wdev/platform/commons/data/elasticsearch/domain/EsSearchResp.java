package cc.wdev.platform.commons.data.elasticsearch.domain;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class EsSearchResp<T> {

    /**
     * 深度分页Id
     */
    private String scrollId;

    /**
     * 总数
     */
    private Long total = 0L;

    /**
     * 查询参数对象
     */
    private List<T> data = Collections.emptyList();
}
