package cc.wdev.platform.system.commons.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class SimpleOptionVo implements Serializable {
    private String title;
    private String label;
    private String labelKey;
    private String labelGroup;
    private String value;
    private Integer level;
}
