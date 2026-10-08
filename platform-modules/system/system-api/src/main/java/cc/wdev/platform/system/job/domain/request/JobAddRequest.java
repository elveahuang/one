package cc.wdev.platform.system.job.domain.request;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class JobAddRequest implements Serializable {
    /**
     * 编号
     */
    private String code;
    /**
     * 线程类名
     */
    private String classname;
    /**
     * 描述说明
     */
    private String description;
    /**
     * 类型
     */
    private String type;
    /**
     * 单位
     */
    private String unit;
    /**
     * 周期
     */
    private Integer period;
    /**
     * 小时
     */
    private Integer hour;
    /**
     * 分钟
     */
    private Integer minute;
    /**
     * 表达式
     */
    private String cron;
    /**
     * 参数
     */
    private String params;
    /**
     * 状态
     */
    private Integer status;
}
