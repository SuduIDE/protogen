package org.sudu.protogen.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbstractChain<T extends AbstractChain<T>> {

    private @Nullable T next;

    public void setNext(@NotNull T next) {
        this.next = next;
    }

    @Nullable
    public T getNext() {
        return next;
    }

    @SafeVarargs
    public static <T extends AbstractChain<T>> T buildChain(T... chains) {
        for (int i = 0; i < chains.length - 1; ++i) {
            var current = chains[i];
            var next = chains[i + 1];
            current.setNext(next);
        }
        return chains[0];
    }
}
