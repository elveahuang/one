package cc.wdev.platform.commons.ai.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量库类型
 *
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum AiVectorStoreType implements BaseEnum<String> {
    PGVECTOR("PGVECTOR", "PGvector", true),
    MARIADB("MARIADB", "MARIADB", true),
    ELASTICSEARCH("ELASTICSEARCH", "Elasticsearch", true);

    private final String value;

    private final String description;

    private final boolean enabled;

}
