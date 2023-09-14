package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;

public class AbstractServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    public AbstractServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.generatedName())
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .addParameters(parameters())
                .addParameter(ParameterSpec.builder(observerType(), "responseObserver").build())
                .build();
    }

    private TypeName observerType() {
        return ParameterizedTypeName.get(
                ClassName.get("io.grpc.stub", "StreamObserver"),
                method.getOutputType().isDomain()
                        ? method.getOutputType().getDomainTypeName(context.configuration().namingManager())
                        : method.getOutputType().getProtobufTypeName()
        );
    }

    protected List<ParameterSpec> parameters() {
        return method.getInputType().getFields().stream()
                .map(f -> new FieldGenerator(context, f).generate())
                .filter(FieldProcessingResult::isNonEmpty)
                .map(FieldProcessingResult::field)
                .map(Poem::fieldToParameter)
                .toList();
    }
}
