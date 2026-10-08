package cc.wdev.platform.commons.data.elasticsearch.callback;

import cc.wdev.platform.commons.core.sequence.Sequence;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.data.elasticsearch.core.event.BeforeConvertCallback;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

/**
 * @author elvea
 */
public class ElasticSearchBeforeSaveCallback<T extends IdEntity> implements BeforeConvertCallback<T>, Ordered {

    private final Sequence sequence;

    public ElasticSearchBeforeSaveCallback(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public @NotNull T onBeforeConvert(T entity, @NotNull IndexCoordinates index) {
        if (entity.getId() == null) {
            entity.setId(this.sequence.nextId());
        }
        return entity;
    }

}
