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

    private String chatModelProvider;

    private String chatServiceProvider;

    private String embeddingModelProvider;

    private String embeddingServiceProvider;

    private String rerankModelProvider;

    private String rerankServiceProvider;

    private String transcriptionModelProvider;

    private String transcriptionServiceProvider;

    private String speechModelProvider;

    private String speechServiceProvider;

    private String imageModelProvider;

    private String imageServiceProvider;

}
