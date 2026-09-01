package cc.wdev.platform.system.core.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 身份类型
 *
 * @author elvea
 */
@Data
@Builder
public class EntityVo implements Serializable {
    private String type;
    private Long id;
    private String username;
    private String displayName;
}
