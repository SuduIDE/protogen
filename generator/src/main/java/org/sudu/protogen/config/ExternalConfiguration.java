package org.sudu.protogen.config;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ExternalConfiguration(
        @Nullable Integer indentationSize,
        @Nullable List<RegisteredTransformer> registeredTransformers
) {

    public static ExternalConfiguration EMPTY = new ExternalConfiguration(null, null);

    public Configuration merge(Configuration current) {
        return new Configuration(
                current.namingManager(),
                Optional.ofNullable(indentationSize).orElse(current.indentationSize()),
                current.nullableAnnotationClass(),
                current.nonnullAnnotationClass(),
                new ArrayList<>(
                        Stream.concat(
                                current.registeredTransformers().stream(),
                                Optional.ofNullable(registeredTransformers).stream().flatMap(Collection::stream)
                        ).collect(Collectors.toMap(
                                RegisteredTransformer::protoType,
                                Function.identity(),
                                this::mergeTransformers
                        )).values()
                )
        );
    }

    private RegisteredTransformer mergeTransformers(RegisteredTransformer stored, RegisteredTransformer current) {
        return stored;
    }

    public interface Parser {
        ExternalConfiguration parse();
    }
}
