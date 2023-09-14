package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.message.FieldTransformerGenerator;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Stream;

public class OverriddenServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    public OverriddenServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.getName())
                .returns(TypeName.VOID)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addParameters(parameters())
                .addCode(body())
                .build();
    }

    private CodeBlock body() {
        return CodeBlock.builder()
                .addStatement("$L($L)",
                        method.generatedName(), requestParams())
                .build();
    }

    private CodeBlock observer() {
        if (method.getOutputType().isDomain()) {
            return CodeBlock.of("$L", TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get("io.grpc.stub", "StreamObserver"),
                            domainTypeName()
                    ))
                    .addMethod(onNext())
                    .addMethod(onError())
                    .addMethod(onCompleted())
                    .build());
        } else {
            return CodeBlock.of("responseObserver");
        }
    }

    private MethodSpec onCompleted() {
        return MethodSpec.methodBuilder("onCompleted")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("responseObserver.onCompleted()")
                .build();
    }

    private MethodSpec onError() {
        return MethodSpec.methodBuilder("onError")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(Throwable.class), "t").build())
                .addStatement("responseObserver.onError(t)")
                .build();
    }

    private MethodSpec onNext() {
        return MethodSpec.methodBuilder("onNext")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(domainTypeName(), "value").build())
                .addStatement("responseObserver.onNext(value.toGrpc())")
                .build();
    }

    private CodeBlock requestParams() {
        List<FieldProcessingResult> processedFields = method.getInputType().getFields().stream()
                .map(field -> new FieldGenerator(context, field).generate())
                .filter(FieldProcessingResult::isNonEmpty)
                .toList();
        return Poem.separatedSequence(
                Stream.concat(
                        processedFields.stream()
                                .map(f -> new FieldTransformerGenerator(f.type(), f.original().getName(), f.isNullable()).fromGrpc("request")),
                        Stream.of(observer())
                ).toList(),
                ",\n"
        );
    }

    private Iterable<ParameterSpec> parameters() {
        return List.of(
                ParameterSpec.builder(method.getInputType().getProtobufTypeName(), "request").build(),
                ParameterSpec.builder(ParameterizedTypeName.get(
                        ClassName.get("io.grpc.stub", "StreamObserver"),
                        method.getOutputType().getProtobufTypeName()
                ), "responseObserver").build()
        );
    }

    private ClassName domainTypeName() {
        return method.getOutputType().getDomainTypeName(context.configuration().namingManager());
    }
}
