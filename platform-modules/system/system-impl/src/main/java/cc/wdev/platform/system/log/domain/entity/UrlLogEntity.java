package cc.wdev.platform.system.log.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Belly
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sys_url_log")
@Data
public class UrlLogEntity extends SimpleEntity {
    /**
     * 路径
     */
    private String path;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 执行时长
     */
    private String execTime;

}
