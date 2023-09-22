package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.message.ToGrpcMethodGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

import java.util.List;

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
        return MethodSpec.methodBuilder(method.generatedName())
                .addModifiers(method.getAccessModifier())
                .returns(returnType.getTypeName())
                .addParameters(params)
                .addCode(body(params))
                .addAnnotation(
                        method.isNullable()
                                ? context.configuration().nullableAnnotationClass()
                                : context.configuration().nonnullAnnotationClass()
                )
                .build();
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
        CodeBlock returnExpr = CodeBlock.of("");
        CodeBlock body = CodeBlock.of("");
        ClassName requestProtoType = method.getInputType().getProtobufTypeName();
        if (requestType != null && !method.doUnfoldRequest()) {
            body = CodeBlock.builder().addStatement("$T grpcRequest = $L", requestProtoType, requestType.toGrpcTransformer(CodeBlock.of("request"))).build();
        } else {
            if (requestType != null) {
                ClassName inputType = method.getInputType().getDomainTypeName(context.configuration().namingManager());

                List<CodeBlock> paramsBlocks = params.stream().map(p -> CodeBlock.of("$N", p)).toList();
                CodeBlock paramsAsList = Poem.separatedSequence(paramsBlocks, ",$W");
                body = CodeBlock.builder()
                        .addStatement("$T grpcRequest = new $T($L).toGrpc()", requestProtoType, inputType, paramsAsList)
                        .build();
            } else {
                List<FieldProcessingResult> processedFields = method.getInputType().getFields().stream()
                        .map(field -> new FieldGenerator(context, field).generate())
                        .filter(FieldProcessingResult::isNonVoid)
                        .toList();
                CodeBlock builder = new ToGrpcMethodGenerator(context, requestProtoType, processedFields, false).builder("requestBuilder");
                body = CodeBlock.builder()
                        .add(builder)
                        .addStatement("$T grpcRequest = requestBuilder.build()", requestProtoType)
                        .build();
            }
        }
        returnExpr = CodeBlock.of("$LStubCall(grpcRequest)", method.generatedName());
        if (returnType.getTypeName() != TypeName.VOID) {
            returnExpr = CodeBlock.of("return $L", returnExpr);
        }
        return CodeBlock.builder().add(body).addStatement(returnExpr).build();
    }
}
