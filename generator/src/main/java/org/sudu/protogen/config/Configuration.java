package org.sudu.protogen.config;

import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.config.naming.NamingManager;
import org.sudu.protogen.config.naming.SuduNamingManager;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Configuration(
        @NotNull NamingManager namingManager,
        int indentationSize,
        @NotNull ClassName nullableAnnotationClass,
        @NotNull ClassName nonnullAnnotationClass,
        @NotNull List<RegisteredTransformer> registeredTransformers,
        @NotNull Map<String, FileConfiguration> filesConfiguration
) {

    interface Parser {
        Configuration parse();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        NamingManager namingManager = new SuduNamingManager();
        int indentationSize = 4;
        ClassName nullableAnnotationClass = ClassName.get("org.jetbrains.annotations", "Nullable");
        ClassName nonnullAnnotationClass = ClassName.get("org.jetbrains.annotations", "NotNull");
        List<RegisteredTransformer> registeredTransformers = RegisteredTransformer.defaultTransformers();
        Map<String, FileConfiguration> filesConfiguration = new HashMap<>();

        public Configuration build() {
            return new Configuration(namingManager, indentationSize, nullableAnnotationClass,
                    nonnullAnnotationClass, registeredTransformers, filesConfiguration);
        }

        public Builder merge(GeneralConfiguration generalConfiguration) {
            Optional.ofNullable(generalConfiguration.indentationSize)
                    .ifPresent(this::indentationSize);
            Optional.ofNullable(generalConfiguration.registeredTransformers)
                    .ifPresent(this::addRegisteredTransformers);
            return this;
        }

        public Builder indentationSize(int indentationSize) {
            this.indentationSize = indentationSize;
            return this;
        }

        public Builder addRegisteredTransformers(List<RegisteredTransformer> transformers) {
            this.registeredTransformers = new ArrayList<>(
                    Stream.concat(registeredTransformers.stream(), transformers.stream())
                            .collect(Collectors.toMap(
                                    RegisteredTransformer::protoType,
                                    Function.identity(),
                                    (oldValue, newValue) -> newValue
                            )).values()
            );
            return this;
        }

        public Builder addFileConfiguration(String filename, FileConfiguration fileConfiguration) {
            filesConfiguration.put(filename, fileConfiguration);
            return this;
        }
    }
}