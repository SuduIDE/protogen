package org.sudu.protogen.utils;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;

public class Poem {

    @NotNull
    public static ParameterSpec fieldToParameter(@NotNull FieldSpec field) {
        return ParameterSpec.builder(field.type, field.name).addAnnotations(field.annotations).build();
    }

    @NotNull
    public static CodeBlock separatedSequence(@NotNull Iterable<CodeBlock> blocks, @NotNull String separator) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (var it = blocks.iterator(); it.hasNext(); ) {
            builder.add(it.next());
            if (it.hasNext()) builder.add(separator);
        }
        return builder.build();
    }

    @NotNull
    public static CodeBlock separatedStringSequence(@NotNull Iterable<String> blocks, @NotNull String separator) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (var it = blocks.iterator(); it.hasNext(); ) {
            builder.add(it.next());
            if (it.hasNext()) builder.add(separator);
        }
        return builder.build();
    }

    @NotNull
    public static ParameterSpec parameter(@NotNull TypeName type, @NotNull String name) {
        return ParameterSpec.builder(type, name).build();
    }

    @NotNull
    public static ClassName className(@NotNull String fullyQualifiedName) {
        return ClassName.get(Name.getPackage(fullyQualifiedName), Name.getLastName(fullyQualifiedName));
    }

    public static void attachNullabilityAnnotations(
            FieldSpec.Builder builder,
            GenerationContext context,
            boolean isNullable
    ) {
        if (isNullable) {
            builder.addAnnotation(context.configuration().nullableAnnotationClass());
        } else {
            builder.addAnnotation(context.configuration().nonnullAnnotationClass());
        }
    }
}
