package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.message.FieldTransformerGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;

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
        this.requestType = context.processType(method.getInputType());
        this.responseType = context.processType(method.getOutputType());
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.getName())
                .returns(TypeName.VOID)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameters(generateMethodParameters())
                .addCode(generateBody())
                .build();
    }

    private TypeModel responseTypeModel() {
        if (responseType != null) {
            return responseType;
        }
        if (method.doUnfoldResponse(responseType)) {
            return new UnfoldedType(context.processType(method.unfoldedResponseField()), method.getOutputType());
        }
        return new TypeModel(method.getOutputType().getProtobufTypeName());
    }

    @NotNull
    private CodeBlock generateBody() {
        return CodeBlock.of("""
                try {
                    $L
                } catch (Throwable $$t) { responseObserver.onError($$t); }
                finally { responseObserver.onCompleted(); }
                """, generateAbstractMethodCall());
    }

    private CodeBlock generateAbstractMethodCall() {
        CodeBlock requestCallParams = requestCallParams();
        if (method.isOutputStreaming()) {
            if (!requestCallParams.equals(CodeBlock.of("")))
                requestCallParams = requestCallParams.toBuilder().add(",$W").build();
            return CodeBlock.of("$N($L(value) -> responseObserver.onNext($L));",
                    abstractMethodSpec,
                    requestCallParams,
                    responseTypeModel().toGrpcTransformer(CodeBlock.of("value"))
            );
        } else {
            CodeBlock methodCall = CodeBlock.of("$N($L)", abstractMethodSpec, requestCallParams);
            methodCall = responseTypeModel().toGrpcTransformer(methodCall);
            return CodeBlock.of("responseObserver.onNext($L);", methodCall);
        }
    }

    private CodeBlock requestCallParams() {
        if (requestType == null || method.doUnfoldRequest()) {
            List<CodeBlock> unfoldedRequestFields = FieldGenerator.generateSeveral(method.getInputType().getFields(), context)
                    .map(f -> new FieldTransformerGenerator(f.type(), f.original().getName(), f.isNullable())
                            .fromGrpc("request")).toList();
            return CodeBlock.of("$L", Poem.separatedSequence(unfoldedRequestFields, ",$W"));
        }
        if (requestType.getTypeName() == TypeName.VOID) {
            return CodeBlock.of("");
        }
        return requestType.fromGrpcTransformer(CodeBlock.of("request"));
    }

    private List<ParameterSpec> generateMethodParameters() {
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
