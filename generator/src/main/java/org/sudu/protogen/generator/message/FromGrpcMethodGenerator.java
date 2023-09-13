package org.sudu.protogen.generator.message;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;

public class FromGrpcMethodGenerator {

    private static final String METHOD_NAME = "fromGrpc";

    private final String parameterName;

    private final GenerationContext generationContext;

    private final ClassName generatedType;

    private final ClassName protoType;

    private final List<FieldProcessingResult> processedFields;

    private final boolean annotate;

    public FromGrpcMethodGenerator(
            @NotNull GenerationContext generationContext,
            @NotNull ClassName generatedType,
            @NotNull ClassName protoType,
            @NotNull List<FieldProcessingResult> processedFields,
            boolean annotate
    ) {
        this.generationContext = generationContext;
        this.generatedType = generatedType;
        this.protoType = protoType;
        this.processedFields = processedFields;
        this.annotate = annotate;

        this.parameterName = buildParameterName(generatedType);
    }

    private static String buildParameterName(ClassName generatedType) {
        return StringUtils.uncapitalize(generatedType.simpleName());
    }

    @NotNull
    public MethodSpec generate() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(generatedType)
                .addParameter(parameter())
                .addStatement("return new $T($>\n$L\n$<)", generatedType, params());
        if (annotate) builder.addAnnotation(generationContext.configuration().nonnullAnnotationClass());
        return builder.build();
    }

    private ParameterSpec parameter() {
        ParameterSpec.Builder builder = ParameterSpec.builder(protoType, parameterName);
        if (annotate) builder.addAnnotation(generationContext.configuration().nonnullAnnotationClass());
        return builder.build();
    }

    private CodeBlock params() {
        return Poem.separatedSequence(
                processedFields.stream().map(this::getTransformer).toList(),
                ",\n"
        );
    }

    private CodeBlock getTransformer(FieldProcessingResult f) {
        return new FieldTransformerGenerator(f.type(), f.original().getName(), f.isNullable()).fromGrpc(parameterName);
    }
}
