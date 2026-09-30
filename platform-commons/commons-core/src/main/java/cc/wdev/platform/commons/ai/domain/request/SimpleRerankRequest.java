package cc.wdev.platform.commons.ai.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 文档重排请求
 *
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleRerankRequest extends Request {

    /**
     * 查询文本
     */
    private String query;

    /**
     * 待重排文档文本
     */
    private List<String> documents;

    /**
     * 返回条数，为空使用厂商默认值
     */
    private Integer topN;

}
