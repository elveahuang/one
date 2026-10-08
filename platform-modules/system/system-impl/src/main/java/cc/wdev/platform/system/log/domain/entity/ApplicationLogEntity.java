package cc.wdev.platform.system.log.domain.entity;

import cc.wdev.platform.commons.data.jpa.domain.SimpleEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_application_log")
public class ApplicationLogEntity extends SimpleEntity {
    private String type;
    private String action;
    private String details;
    private String exception;
}
