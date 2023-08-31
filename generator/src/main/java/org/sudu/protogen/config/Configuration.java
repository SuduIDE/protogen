package org.sudu.protogen.config;

import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.config.naming.NamingManager;
import org.sudu.protogen.config.naming.SuduNamingManager;

import java.util.List;

public record Configuration(
        @NotNull NamingManager namingManager,
        int indentationSize,
        @NotNull ClassName nullableAnnotationClass,
        @NotNull ClassName nonnullAnnotationClass,
        @NotNull List<RegisteredTransformer> registeredTransformers
) {

    public static final Configuration DEFAULT = new Configuration(
            new SuduNamingManager(),
            4,
            ClassName.get("org.jetbrains.annotations", "Nullable"),
            ClassName.get("org.jetbrains.annotations", "NotNull"),
            RegisteredTransformer.defaultTransformers()
    );

}