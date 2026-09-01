package cc.wdev.platform.system.core.domain.dto;

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
public class OrganizationSaveDto implements Serializable {
    private Long id;
    private Long parentId;
    private String code;
    private String title;
    private String label;
    private String description;
}
