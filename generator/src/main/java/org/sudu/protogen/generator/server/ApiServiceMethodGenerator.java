package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;

public class ApiServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    private final @Nullable TypeModel requestType;

    private final @Nullable TypeModel responseType;

    public ApiServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
        this.requestType = context.typeManager().processType(method.getInputType());
        this.responseType = context.typeManager().processType(method.getOutputType());
    }

    public MethodSpec generate() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.generatedName())
                .addModifiers(Modifier.PROTECTED)
                .addParameters(generateRequestParameters())
                .addCode(CodeBlock.of(
                        "throw $T.UNIMPLEMENTED.withDescription(\"Method $L is not implemented\").asRuntimeException();",
                        ClassName.get("io.grpc", "Status"),
                        method.getName()
                ));
        specifyResponseWay(builder);
        return builder.build();
    }

    /**
     * Either adds a return type or a {@code StreamObserver<Response> } parameter
     */
    private void specifyResponseWay(MethodSpec.Builder methodBuilder) {
        TypeName returnType = responseType();
        if (method.isOutputStreaming()) {
            methodBuilder.returns(TypeName.VOID);
            methodBuilder.addParameter(generateObserverParameter(returnType));
        } else {
            if (!returnType.isPrimitive() && method.getContainingFile().doUseNullabilityAnnotation(false)) {
                methodBuilder.addAnnotation(context.configuration().nonnullAnnotationClass());
            }
            methodBuilder.returns(returnType);
        }
    }

    private TypeName responseType() {
        if (responseType != null) {
            return responseType.getTypeName();
        }
        if (method.doUnfoldResponse(responseType)) {
            var field = method.unfoldedResponseField();
            return context.typeManager().processType(field).getTypeName();
        }
        return method.getOutputType().getProtobufTypeName();
    }

    private Iterable<ParameterSpec> generateRequestParameters() {
        if (requestType == null || method.doUnfoldRequest()) {
            return method.getInputType().getFields().stream()
                    .map(field -> context.generatorsHolder().generate(field))
                    .filter(FieldProcessingResult::isNonVoid)
                    .map(FieldProcessingResult::field)
                    .map(Poem::fieldToParameter)
                    .toList();
        }
        if (requestType.getTypeName() == TypeName.VOID) {
            return List.of();
        }
        return List.of(ParameterSpec.builder(requestType.getTypeName(), "request").addAnnotation(NotNull.class).build());
    }

    private ParameterSpec generateObserverParameter(TypeName responseType) {
        TypeName observerType = ParameterizedTypeName.get(
                ClassName.get("java.util.function", "Consumer"),
                responseType.box()
        );
        return ParameterSpec.builder(observerType, "responseConsumer").addAnnotation(NotNull.class).build();
    }
}