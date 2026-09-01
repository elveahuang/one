package cc.wdev.platform.system.job.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author Belly
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_job")
@Data
public class JobEntity extends BaseEntity {
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
     * 状态
     */
    private Integer status;

    /**
     * 参数
     */
    private String params;
}
