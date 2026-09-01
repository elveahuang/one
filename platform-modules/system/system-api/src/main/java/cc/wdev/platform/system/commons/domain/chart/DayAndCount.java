package cc.wdev.platform.system.commons.domain.chart;

import lombok.Builder;
import lombok.Data;

/**
 * 会话统计
 *
 * @author elvea
 */
@Data
@Builder
public class DayAndCount {
    /**
     * 键
     */
    private int key;
    /**
     * 计数
     */
    private int count;
}
