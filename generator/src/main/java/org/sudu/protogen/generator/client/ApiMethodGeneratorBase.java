package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.EmptyIfNotFound;
import org.sudu.protogen.NullifyIfNotFound;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.message.ToGrpcMethodGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;
import protogen.Options;

import java.util.List;
import java.util.Objects;

public class ApiMethodGeneratorBase {

    private final GenerationContext context;

    private final Method method;

    private final TypeModel returnType;

    private final @Nullable TypeModel requestType;

    public ApiMethodGeneratorBase(GenerationContext context, Method method, TypeModel returnType, @Nullable TypeModel requestType) {
        this.context = context;
        this.method = method;
        this.returnType = returnType;
        this.requestType = requestType;
    }

    public MethodSpec generate() {
        List<ParameterSpec> params = params();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.generatedName())
                .addModifiers(method.getAccessModifier())
                .returns(returnType.getTypeName())
                .addParameters(params)
                .addCode(body(params))
                .addAnnotation(
                        method.ifNotFoundBehavior() == Options.IfNotFound.NULLIFY
                                ? context.configuration().nullableAnnotationClass()
                                : context.configuration().nonnullAnnotationClass()
                );
        switch (method.ifNotFoundBehavior()) {
            case NULLIFY -> builder.addAnnotation(NullifyIfNotFound.class);
            case EMPTY -> builder.addAnnotation(EmptyIfNotFound.class);
        }
        return builder.build();
    }

    private List<ParameterSpec> params() {
        if (requestType != null) {
            if (requestType.getTypeName() == TypeName.VOID) {
                return List.of();
            }
            if (!method.doUnfoldRequest()) {
                return List.of(ParameterSpec.builder(requestType.getTypeName(), "request").build());
            }
        }
        // Unfolds request into a fields list
        return FieldGenerator.generateSeveral(method.getInputType().getFields(), context)
                .map(FieldProcessingResult::field)
                .map(Poem::fieldToParameter)
                .toList();
    }

    private CodeBlock body(List<ParameterSpec> params) {
        CodeBlock returnExpr = CodeBlock.of("$LStubCall(grpcRequest)", method.generatedName());
        if (returnType.getTypeName() != TypeName.VOID) {
            returnExpr = CodeBlock.of("return $L", returnExpr);
        }
        return CodeBlock.builder().add(buildRequest(params)).addStatement(returnExpr).build();
    }

    private CodeBlock buildRequest(List<ParameterSpec> params) {
        ClassName requestProtoType = method.getInputType().getProtobufTypeName();
        if (requestType != null) {
            if (!method.doUnfoldRequest()) {
                return buildDomainRequest(requestProtoType);
            } else {
                return buildUnfoldedDomainRequest(params, requestProtoType);
            }
        } else {
            return buildNonDomainRequest(requestProtoType);
        }
    }

    @NotNull
    private CodeBlock buildNonDomainRequest(ClassName requestProtoType) {
        List<FieldProcessingResult> processedFields = method.getInputType().getFields().stream()
                .map(field -> context.generatorsHolder().field(field))
                .filter(FieldProcessingResult::isNonVoid)
                .toList();
        CodeBlock builder = new ToGrpcMethodGenerator(context, requestProtoType, processedFields, false).builder("requestBuilder");
        return CodeBlock.builder()
                .add(builder)
                .addStatement("$T grpcRequest = requestBuilder.build()", requestProtoType)
                .build();
    }

    @NotNull
    private CodeBlock buildUnfoldedDomainRequest(List<ParameterSpec> params, ClassName requestProtoType) {
        ClassName inputType = method.getInputType().getDomainTypeName(context.configuration().namingManager());

        List<CodeBlock> paramsBlocks = params.stream().map(p -> CodeBlock.of("$N", p)).toList();
        CodeBlock paramsAsList = Poem.separatedSequence(paramsBlocks, ",$W");
        return CodeBlock.of("$T grpcRequest = $L;\n",
                requestProtoType,
                Objects.requireNonNull(requestType).toGrpcTransformer(
                        CodeBlock.of("new $T($L)", inputType, paramsAsList)
                )
        );
    }

    @NotNull
    private CodeBlock buildDomainRequest(ClassName requestProtoType) {
        return CodeBlock.of("$T grpcRequest = $L;\n",
                requestProtoType,
                Objects.requireNonNull(requestType).toGrpcTransformer(CodeBlock.of("request"))
        );
    }
}
