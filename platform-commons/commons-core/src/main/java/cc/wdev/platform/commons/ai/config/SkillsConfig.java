package cc.wdev.platform.commons.ai.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillsConfig implements Serializable {

    @Builder.Default
    private boolean enabled = false;

    @Builder.Default
    private List<Resource> paths = List.of(new ClassPathResource("META-INF/cc.wdev/skills"));

}
