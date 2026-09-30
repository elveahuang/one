package cc.wdev.platform.commons.ai.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import cn.hutool.core.io.resource.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleSpeechRequest extends Request {

    /**
     * Request ID
     */
    private String requestId;

    private Resource resource;

}
