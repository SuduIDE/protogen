package org.sudu.protogen.utils;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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

    public static Collector<CodeBlock, CodeBlock.Builder, CodeBlock> joinCodeBlocks() {
        return joinCodeBlocks("");
    }

    public static Collector<CodeBlock, CodeBlock.Builder, CodeBlock> joinCodeBlocks(String sep) {
        return new Collector<>() {
            @Override
            public Supplier<CodeBlock.Builder> supplier() {
                return CodeBlock::builder;
            }

            @Override
            public BiConsumer<CodeBlock.Builder, CodeBlock> accumulator() {
                return (builder, block) -> {
                    if (!builder.isEmpty()) builder.add(sep);
                    builder.add(block);
                };
            }

            @Override
            public BinaryOperator<CodeBlock.Builder> combiner() {
                return (builder1, builder2) -> builder1.add(sep).add(builder2.build());
            }

            @Override
            public Function<CodeBlock.Builder, CodeBlock> finisher() {
                return CodeBlock.Builder::build;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };
    }
}
