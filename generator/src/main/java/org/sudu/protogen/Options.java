package org.sudu.protogen;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.GeneratedMessageV3;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Options {

    @SuppressWarnings("rawtypes")
    public static List<GeneratedExtension> getOptionsExtensions() {
        return Arrays.stream(protogen.Options.class.getFields())
                .filter(field -> field.getType() == GeneratedExtension.class)
                .map(field -> {
                    try {
                        return (GeneratedExtension) field.get(null);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static <U extends GeneratedMessageV3.ExtendableMessage<U>, T> Optional<T> wrapExtension(
            U options,
            GeneratedExtension<U, T> extension
    ) {
        if (options.hasExtension(extension)) {
            return Optional.of(options.getExtension(extension));
        }
        return Optional.empty();
    }
}
