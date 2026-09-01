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
public class ServiceProviderConfig implements Serializable {

    private String text;

    private String image;

    private String transcription;

    private String speech;

    private String embedding;

    private String rerank;

}
