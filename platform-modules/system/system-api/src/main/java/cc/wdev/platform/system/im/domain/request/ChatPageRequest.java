package cc.wdev.platform.system.im.domain.request;

import cc.wdev.platform.commons.web.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ChatPageRequest extends PageRequest {
    /**
     * 标签页
     */
    protected String tab;
}
