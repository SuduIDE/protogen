package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerationHelper;
import org.sudu.protogen.generator.type.TypeModel;
import protogen.Options;

import javax.lang.model.element.Modifier;
import java.util.List;

public class ApiServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    private final @Nullable TypeModel processedRequestType;

    private final @Nullable TypeModel processedResponseType;

    public ApiServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
        this.processedRequestType = context.typeManager().processType(method.getInputType());
        this.processedResponseType = context.typeManager().processType(method.getOutputType());
    }

    public MethodSpec generate() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.generatedName())
                .addModifiers(Modifier.PROTECTED)
                .addParameters(generateRequestParameters());
        throwUnimplemented(builder);
        specifyResponseWay(builder);
        return builder.build();
    }

    private void throwUnimplemented(MethodSpec.Builder builder) {
        builder.addCode(CodeBlock.of(
                "throw $T.UNIMPLEMENTED.withDescription(\"Method $L is not implemented\").asRuntimeException();",
                ClassName.get("io.grpc", "Status"),
                method.getName()
        ));
    }

    /**
     * Adds either a return type or a {@code StreamObserver<Response> } parameter
     */
    private void specifyResponseWay(MethodSpec.Builder methodBuilder) {
        TypeModel returnType = responseType();
        if (method.isOutputStreaming()) {
            methodBuilder.returns(TypeName.VOID);
            methodBuilder.addParameter(buildConsumerParameter(returnType));
        } else {
            if (!returnType.isPrimitiveOrVoid() && method.getContainingFile().doUseNullabilityAnnotation(false)) {
                if (method.ifNotFoundBehavior() == Options.IfNotFound.NULLIFY) {
                    methodBuilder.addJavadoc("Null result is converted to Status.NOT_FOUND");
                    methodBuilder.addAnnotation(context.configuration().nullableAnnotationClass());
                } else {
                    methodBuilder.addAnnotation(context.configuration().nonnullAnnotationClass());
                }
            }
            methodBuilder.returns(returnType.getTypeName());
        }
    }

    private TypeModel responseType() {
        if (processedResponseType != null) {
            return processedResponseType;
        }
        if (method.doUnfoldResponse(processedResponseType)) {
            var field = method.unfoldedResponseField();
            return context.typeManager().processType(field);
        }
        return new TypeModel(method.getOutputType().getProtobufTypeName()); // todo make getProtobufTypeName returning TypeModel
    }

    private Iterable<ParameterSpec> generateRequestParameters() {
        if (processedRequestType == null || method.doUnfoldRequest()) {
            return FieldGenerationHelper.processFieldsToParameters(method.getInputType(), context);
        }
        if (processedRequestType.getTypeName() == TypeName.VOID) {
            return List.of();
        }
        return List.of(ParameterSpec.builder(processedRequestType.getTypeName(), "request").addAnnotation(NotNull.class).build());
    }

    private ParameterSpec buildConsumerParameter(TypeModel responseType) {
        TypeName observerType = ParameterizedTypeName.get(
                ClassName.get("java.util.function", "Consumer"),
                responseType.getTypeName().box()
        );
        return ParameterSpec.builder(observerType, "responseConsumer").addAnnotation(NotNull.class).build();
    }
}
