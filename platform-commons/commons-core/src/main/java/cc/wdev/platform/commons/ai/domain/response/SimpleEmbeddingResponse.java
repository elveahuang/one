package cc.wdev.platform.commons.ai.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleEmbeddingResponse<E> implements Serializable {

    /**
     * Request ID
     */
    private String requestId;

    /**
     * Task ID
     */
    private String taskId;

    /**
     * 接口响应结构
     */
    private E result;

}
