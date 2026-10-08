package cc.wdev.platform.system.catalog.domain.request;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class CatalogSaveRequest implements Serializable {
    private Long id;
    private Long parentId;
    private String bizType;
    private Long bizId;
    private String code;
    private String title;
    private String description;
}
