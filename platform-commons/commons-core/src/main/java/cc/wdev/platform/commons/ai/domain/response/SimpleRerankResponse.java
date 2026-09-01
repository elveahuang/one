package cc.wdev.platform.commons.ai.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文档重排响应
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRerankResponse<E> implements Serializable {

    /**
     * Request ID
     */
    private String requestId;

    /**
     * Task ID
     */
    private String taskId;

    /**
     * 文档文本
     */
    private List<String> documents;

    /**
     * 接口响应结构
     */
    private E result;

}
