package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.config.*;
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
public class AiConfig implements Serializable {

    private ServiceProviderConfig service;

    private ServiceProviderConfig factory;

    private VectorStoreConfig vectorStore;

    private SplittingConfig splitting;

    private RetrievalConfig retrieval;

    private VectorizationConfig vectorization;

    private WorkSpaceConfig workspace;

    private SkillConfig skill;

}
