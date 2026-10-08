package cc.wdev.webapp.es.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CourseDto implements Serializable {
    /**
     * 课程ID
     */
    private String id;
    /**
     * 课程名称
     */
    private String title;
}
