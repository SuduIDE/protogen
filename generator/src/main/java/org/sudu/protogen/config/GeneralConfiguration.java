package org.sudu.protogen.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeneralConfiguration {

    @Nullable Integer indentationSize;

    @Nullable List<RegisteredTransformer> registeredTransformers;

    @Nullable String nullableAnnotation;

    @Nullable String notNullAnnotation;

    @Override
    public String toString() {
        return "GeneralConfiguration{" +
                "indentationSize=" + indentationSize +
                ", registeredTransformers=" + registeredTransformers +
                ", nullableAnnotation='" + nullableAnnotation + '\'' +
                ", notNullAnnotation='" + notNullAnnotation + '\'' +
                '}';
    }
}
