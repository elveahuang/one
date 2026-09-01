package cc.wdev.platform.system.config.domain.request;

import cc.wdev.platform.commons.web.request.Request;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author irving
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BizTypeListRequest extends Request {
    private String bizGroupType;
    private String bizType;
}
