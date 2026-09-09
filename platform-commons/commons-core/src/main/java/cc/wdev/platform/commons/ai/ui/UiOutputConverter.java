package cc.wdev.platform.commons.ai.ui;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;

/**
 * @author elvea
 */
public class UiOutputConverter implements StructuredOutputConverter<UiResponse> {
    private final BeanOutputConverter<UiResponse> delegate = new BeanOutputConverter<>(UiResponse.class);
    private final String schema;

    public UiOutputConverter(String schema) {
        this.schema = schema;
    }

    @Override
    public UiResponse convert(@NonNull String source) {
        return delegate.convert(source);
    }

    @Override
    public @NonNull String getFormat() {
        return "Return JSON only and conform exactly to this schema:\n" + schema;
    }

    @Override
    public @NonNull String getJsonSchema() {
        return schema;
    }

}
