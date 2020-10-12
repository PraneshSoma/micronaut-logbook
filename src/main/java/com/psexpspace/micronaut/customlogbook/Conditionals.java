package com.psexpspace.micronaut.customlogbook;

import org.zalando.fauxpas.ThrowingConsumer;

final class Conditionals {

    private Conditionals() {
        // nothing to do
    }

    static <T, X extends Exception> void runIf(
            final Object object,
            final Class<T> type,
            final ThrowingConsumer<T, X> consumer) {

        if (type.isInstance(object)) {
            consumer.accept(type.cast(object));
        }
    }

}
