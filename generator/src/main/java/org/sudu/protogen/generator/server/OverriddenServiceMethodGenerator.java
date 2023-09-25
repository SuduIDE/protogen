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
 * Example:
 * <pre>{@code
 * @Override
 * public void someMethod(GrpcSomeRequest request, StreamObserver<GrpcSomeDomain> responseObserver) {
 *  someMethod(
 *      request.getA(),
 *      request.getB(),
 *      new StreamObserver<SomeDomain>() {
 *
 *          public void onNext(SomeDomain value) {
 *              responseObserver.onNext(value.toGrpc());
 *          }
 *
 *          public void onError(Throwable t) {
 *              responseObserver.onError(t);
 *          }
 *
 *          public void onCompleted() {
 *              responseObserver.onCompleted();
 *          }
 *      }
 *  );
 * }
 * }</pre>
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
        return null;
    }

    @NotNull
    private CodeBlock generateBody() {
        CodeBlock abstractMethodCall = CodeBlock.of("$N($L)", abstractMethodSpec, generateAbstractMethodCallParams());
        if (method.isOutputStreaming()) {
            return CodeBlock.builder().addStatement(abstractMethodCall).build();
        } else {
            CodeBlock grpcType = abstractMethodCall;
            TypeModel responsedTypeModel = responseTypeModel();
            if (responsedTypeModel != null) {
                grpcType = responsedTypeModel.toGrpcTransformer(grpcType);
            }
            return CodeBlock.of("""
                    responseObserver.onNext($L);
                    responseObserver.onCompleted();
                    """, grpcType);
        }
    }

    /**
     * Either unfolds request params into list of fields, or returns a domain object
     */
    private CodeBlock generateAbstractMethodCallParams() {
        CodeBlock callParams = requestCallParams();
        if (method.isOutputStreaming()) {
            if (!callParams.equals(CodeBlock.of(""))) callParams = callParams.toBuilder().add(",$W").build();
            callParams = callParams.toBuilder().add("$L", responseObserverCallParam()).build();
        }
        return callParams;
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

    private CodeBlock responseObserverCallParam() {
        if (responseType != null) {
            return CodeBlock.of("$L", new AnonymousStreamObserverGenerator(responseType).generate());
        }
        if (method.doUnfoldResponse(responseType)) {
            var field = method.unfoldedResponseField();
            TypeModel type = new UnfoldedType(
                    context.processType(field),
                    method.getOutputType()
            );
            return CodeBlock.of("$L", new AnonymousStreamObserverGenerator(type).generate());
        }
        return CodeBlock.of("responseObserver");
    }

    /**
     * <pre>
     * Default protobuf stub method parameters:
     * {@code GrpcSomeRequest request, StreamObserver<GrpcSomeResponse> response}
     * </pre>
     */
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

    /**
     * Decorates {@code StreamObserver<GrpcType>} to {@code StreamObserver<Type>}</code>
     * <pre>{@code
     * new StreamObserver<Type>() {
     *
     *  public void onNext(Type value) {
     *      responseObserver.onNext(value.toGrpc());
     *  }
     *
     *  public void onError(Throwable t) {
     *      responseObserver.onError(t);
     *  }
     *
     *  public void onCompleted() {
     *      responseObserver.onCompleted();
     *  }
     * }
     * }</pre>
     */
    private static class AnonymousStreamObserverGenerator {

        private final TypeModel domainType;

        public AnonymousStreamObserverGenerator(TypeModel domainType) {
            this.domainType = domainType;
        }

        public TypeSpec generate() {
            return TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get("io.grpc.stub", "StreamObserver"),
                            domainType.getTypeName().box()
                    ))
                    .addMethod(onNextMethod())
                    .addMethod(onErrorMethod())
                    .addMethod(onCompletedMethod())
                    .build();
        }

        private MethodSpec onCompletedMethod() {
            return MethodSpec.methodBuilder("onCompleted")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("responseObserver.onCompleted()")
                    .build();
        }

        private MethodSpec onErrorMethod() {
            return MethodSpec.methodBuilder("onError")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(ClassName.get(Throwable.class), "t").build())
                    .addStatement("responseObserver.onError(t)")
                    .build();
        }

        private MethodSpec onNextMethod() {
            return MethodSpec.methodBuilder("onNext")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(domainType.getTypeName().box(), "value").build())
                    .addStatement("responseObserver.onNext($L)", domainType.toGrpcTransformer(CodeBlock.of("value")))
                    .build();
        }
    }
}
