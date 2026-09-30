package cc.wdev.platform.commons.utils.builder;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author elvea
 */
public class Builder<T> {

    private final Supplier<T> supplier;

    private Builder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Builder<T> of(Supplier<T> supplier) {
        return new Builder<>(supplier);
    }

    public <P> Builder<T> with(BiConsumer<T, P> consumer, P value) {
        return new Builder<>(() -> {
            T object = supplier.get();
            consumer.accept(object, value);
            return object;
        });
    }

    public T build() {
        return supplier.get();
    }

}
