package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerationHelper;
import org.sudu.protogen.generator.message.FieldTransformerGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;
import org.sudu.protogen.utils.Poem;
import protogen.Options;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Stream;

/**
 * Overrides default stub method to pass request and response as domain objects or list of fields
 */
public class OverriddenServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    private final MethodSpec abstractMethodSpec;

    private final @Nullable TypeModel responseType;

    private final @Nullable TypeModel requestType;

    public OverriddenServiceMethodGenerator(GenerationContext context, Method method, MethodSpec abstractMethodSpec) {
        this.context = context;
        this.method = method;
        this.abstractMethodSpec = abstractMethodSpec;
        this.requestType = context.typeManager().processType(method.getInputType());
        this.responseType = context.typeManager().processType(method.getOutputType());
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.getName())
                .returns(TypeName.VOID)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameters(buildMethodParameters())
                .addCode(generateBody())
                .build();
    }

    private TypeModel responseTypeModel() {
        if (responseType != null) {
            return responseType;
        }
        if (method.doUnfoldResponse(responseType)) {
            return new UnfoldedType(context.typeManager().processType(method.unfoldedResponseField()), method.getOutputType());
        }
        return new TypeModel(method.getOutputType().getProtobufTypeName());
    }

    @NotNull
    private CodeBlock generateBody() {
        return CodeBlock.builder()
                .add(generateExternalMethodCall())
                .addStatement("responseObserver.onCompleted()")
                .build();
    }

    private CodeBlock generateExternalMethodCall() {
        if (method.isOutputStreaming()) {
            return generateStreamingMethodCall();
        } else if (responseType != null && responseType.getTypeName() == TypeName.VOID) {
            return generateVoidReturningMethodCall();
        } else {
            return generateCommonMethodCall();
        }
    }

    @NotNull
    private CodeBlock generateStreamingMethodCall() {
        Stream<CodeBlock> requestCallParams = StreamEx.of(generateRequestCallParams()).append(
                CodeBlock.of("(value) -> responseObserver.onNext($L)", responseTypeModel().toGrpcTransformer(CodeBlock.of("value")))
        );
        return CodeBlock.of("$N($L);\n", abstractMethodSpec, requestCallParams.collect(Poem.joinCodeBlocks(",$W")));
    }

    @NotNull
    private CodeBlock generateVoidReturningMethodCall() {
        CodeBlock methodCall = CodeBlock.of("$N($L)", abstractMethodSpec, generateRequestCallParams().collect(Poem.joinCodeBlocks(",$W")));
        return CodeBlock.builder()
                .addStatement(methodCall)
                .addStatement("responseObserver.onNext($L)", responseTypeModel().toGrpcTransformer(methodCall))
                .build();
    }

    @NotNull
    private CodeBlock generateCommonMethodCall() {
        CodeBlock methodCall = CodeBlock.of("$N($L)", abstractMethodSpec, generateRequestCallParams().collect(Poem.joinCodeBlocks(",$W")));
        CodeBlock nullCheck = CodeBlock.of("""
                        if (result == null) {
                        $>throw $T.NOT_FOUND.withDescription("Method returned null").asRuntimeException();$<
                        }
                        """,
                ClassName.get("io.grpc", "Status")
        );
        return CodeBlock.builder()
                .addStatement("var result = $L", responseTypeModel().toGrpcTransformer(methodCall))
                .addIf(method.ifNotFoundBehavior() == Options.IfNotFound.NULLIFY, nullCheck)
                .addStatement("responseObserver.onNext(result)")
                .build();
    }

    private Stream<CodeBlock> generateRequestCallParams() {
        if (requestType == null || method.doUnfoldRequest()) {
            return FieldGenerationHelper.processAllFields(method.getInputType(), context)
                    .map(f -> new FieldTransformerGenerator(f.type(), f.original().getName(), f.isNullable())
                            .fromGrpc("request"));
        }
        if (requestType.getTypeName() == TypeName.VOID) {
            return Stream.of();
        }
        return Stream.of(requestType.fromGrpcTransformer(CodeBlock.of("request")));
    }

    private List<ParameterSpec> buildMethodParameters() {
        TypeName requestType = method.getInputType().getProtobufTypeName();
        TypeName responseType = method.getOutputType().getProtobufTypeName();
        ParameterizedTypeName responseObserverType = ParameterizedTypeName.get(
                ClassName.get("io.grpc.stub", "StreamObserver"),
                responseType
        );
        return List.of(
                ParameterSpec.builder(requestType, "request").build(),
                ParameterSpec.builder(responseObserverType, "responseObserver").build()
        );
    }
}
