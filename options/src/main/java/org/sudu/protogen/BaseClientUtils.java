package org.sudu.protogen;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class BaseClientUtils {

    public static <T> T nullifyIfNotFound(@NotNull Supplier<T> block) {
        try {
            return block.get();
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
                return null;
            }
            throw ex;
        }
    }

    public static <T> Collection<T> emptyIfNotFound(@NotNull Supplier<Collection<T>> block) {
        try {
            return block.get();
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
                return List.of();
            }
            throw ex;
        }
    }
}
