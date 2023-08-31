package org.sudu.protogen.generator.message;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldProcessingResult;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.UUID;

public class ToGrpcMethodGenerator {

    private static final String METHOD_NAME = "toGrpc";

    private static final List<String> BUILDER_NAMES = List.of("builder", "b", "builder$");

    private final GenerationContext generationContext;

    private final ClassName protoType;

    private final List<FieldProcessingResult> processedFields;

    private final boolean annotate;

    public ToGrpcMethodGenerator(
            @NotNull GenerationContext generationContext,
            @NotNull ClassName protoType,
            @NotNull List<FieldProcessingResult> processedFields,
            boolean annotate
    ) {
        this.generationContext = generationContext;
        this.protoType = protoType;
        this.processedFields = processedFields;
        this.annotate = annotate;
    }

    @NotNull
    public MethodSpec generate() {
        String builderName = resolveBuilderName();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .returns(protoType)
                .addCode(builder(builderName))
                .addStatement("return $L.build()", builderName);
        if (annotate) builder.addAnnotation(generationContext.configuration().nonnullAnnotationClass());
        return builder.build();
    }

    public CodeBlock builder(String builderName) {

        List<CodeBlock> fieldTransformers = processedFields.stream()
                .map(p -> new FieldTransformerGenerator(p.type(), p.original().getName(), p.isNullable()).toGrpc(builderName, p.field().name))
                .toList();

        CodeBlock.Builder methodBuilder = CodeBlock.builder()
                .addStatement("$T.Builder $N = $T.newBuilder()", protoType, builderName, protoType);

        for (CodeBlock t : fieldTransformers) methodBuilder.add(t);

        return methodBuilder.build();
    }


    private String resolveBuilderName() {
        for (String builderName : BUILDER_NAMES) {
            if (processedFields.stream().anyMatch(f -> f.field().name.equals(builderName))) {
                continue;
            }
            return builderName;
        }
        return UUID.randomUUID().toString();
    }
}
