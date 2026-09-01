package cc.wdev.platform.system.core.domain.bo;

import cc.wdev.platform.commons.annotations.DateTimeFormat;
import cc.wdev.platform.commons.annotations.JsonFormat;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EntityDateBo {
    /**
     * 目标ID
     */
    private Long id;
    /**
     * 时间
     */
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime dateTime;
}
