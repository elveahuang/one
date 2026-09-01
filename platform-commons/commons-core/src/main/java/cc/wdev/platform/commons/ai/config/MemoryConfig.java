package cc.wdev.platform.commons.ai.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryConfig implements Serializable {

    @Builder.Default
    private boolean enabled = false;

    private String path;

}
