package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.message.FieldTransformerGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Stream;

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

    private final @Nullable TypeModel responseDomainType;

    private final @Nullable TypeModel requestDomainType;

    public OverriddenServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
        this.requestDomainType = context.typeProcessor().processTypeOrNull(method.getInputType(), context);
        this.responseDomainType = context.typeProcessor().processTypeOrNull(method.getOutputType(), context);
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.getName())
                .returns(TypeName.VOID)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameters(generateMethodParameters())
                .addCode(CodeBlock.of("$L($L);", method.generatedName(), generateAbstractMethodCallParams()))
                .build();
    }

    /**
     * Either unfolds request params into list of fields, or returns a domain object
     */
    private CodeBlock generateAbstractMethodCallParams() {
        if (requestDomainType != null && requestDomainType.getTypeName() == TypeName.VOID) {
            return Poem.separatedSequence(generateResponseObserver(), ",\n");
        }
        if (requestDomainType != null && !method.doUnfoldRequest()) {
            return CodeBlock.of("$L",
                    Poem.separatedSequence(
                            Stream.concat(
                                    Stream.of(requestDomainType.fromGrpcTransformer(CodeBlock.of("request"))),
                                    generateResponseObserver().stream()
                            ).toList(), ",\n"
                    )
            );
        }
        // todo think about how to took out such logic because client does the same
        List<CodeBlock> unfoldedRequestFields = method.getInputType().getFields().stream()
                .map(field -> new FieldGenerator(context, field).generate())
                .filter(FieldProcessingResult::isNonEmpty)
                .map(f -> new FieldTransformerGenerator(f.type(), f.original().getName(), f.isNullable())
                        .fromGrpc("request"))
                .toList();
        return CodeBlock.of("$L",
                Poem.separatedSequence(Stream.concat(unfoldedRequestFields.stream(), generateResponseObserver().stream()).toList(), ",\n")
        );
    }

    /**
     * Creates anonymous ResponseObserver if necessary
     */
    private List<CodeBlock> generateResponseObserver() {
        if (method.getOutputType().getFields().isEmpty()) {
            return List.of();
        }
        if (responseDomainType != null) {
            if (responseDomainType.getTypeName() == TypeName.VOID) return List.of();
            return List.of(CodeBlock.of("$L", new AnonymousStreamObserverGenerator(responseDomainType).generate()));
        }
        if (method.getOutputType().getFields().size() == 1) {
            var field = method.getOutputType().getFields().get(0);
            FieldProcessingResult fpr = new FieldGenerator(context, field).generate();
            return List.of(CodeBlock.of("$L", new AnonymousStreamObserverGenerator(
                    new UnfoldedType(fpr.type(), field.getName(), Poem.className(method.getOutputType().getFullName()))
            ).generate()));
        }
        return List.of(CodeBlock.of("responseObserver"));
    }

    /**
     * <pre>
     * Default protobuf stub method parameters:
     * {@code GrpcSomeRequest request, StreamObserver<GrpcSomeResponse> response}
     * </pre>
     */
    private Iterable<ParameterSpec> generateMethodParameters() {
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
