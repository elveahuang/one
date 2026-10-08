package cc.wdev.platform.commons.ai.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.Resource;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleTranscriptionRequest extends Request {
    /**
     * Request ID
     */
    private String requestId;

    /**
     * Task ID
     */
    private String taskId;

    private String fileUrl;

    private String fileName;

    private Resource resource;
}
