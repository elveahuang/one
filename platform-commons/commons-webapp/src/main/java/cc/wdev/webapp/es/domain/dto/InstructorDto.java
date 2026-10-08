package cc.wdev.webapp.es.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class InstructorDto implements Serializable {
    /**
     * 讲师ID
     */
    private String id;
    /**
     * 讲师名称
     */
    private String name;
}
