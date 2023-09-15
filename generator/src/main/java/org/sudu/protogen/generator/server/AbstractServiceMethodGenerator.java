package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Stream;

public class AbstractServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    private final @Nullable TypeModel requestTypeModel;

    private final @Nullable TypeModel responseTypeModel;

    public AbstractServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
        this.requestTypeModel = context.typeProcessor().processTypeOrNull(method.getInputType(), context);
        this.responseTypeModel = context.typeProcessor().processTypeOrNull(method.getOutputType(), context);
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.generatedName())
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .addParameters(generateMethodParameters())
                .build();
    }

    private List<ParameterSpec> generateObserverParameter() {
        if (responseTypeModel != null && responseTypeModel.getTypeName() == TypeName.VOID) {
            return List.of();
        }
        if (method.getOutputType().getFields().isEmpty()) {
            return List.of();
        }
        TypeName type = ParameterizedTypeName.get(
                ClassName.get("io.grpc.stub", "StreamObserver"),
                returnType().box()
        );
        return List.of(ParameterSpec.builder(type, "responseObserver").build());
    }

    private TypeName returnType() {
        if (responseTypeModel != null) {
            return responseTypeModel.getTypeName();
        }
        if (method.getOutputType().getFields().size() == 1) {
            var field = method.getOutputType().getFields().get(0);
            FieldProcessingResult fpr = new FieldGenerator(context, field).generate();
            return fpr.type().getTypeName();
        }
        return method.getOutputType().getProtobufTypeName();
    }

    protected Iterable<ParameterSpec> generateMethodParameters() {
        if (requestTypeModel != null && requestTypeModel.getTypeName() == TypeName.VOID) {
            return generateObserverParameter();
        }
        if (requestTypeModel != null && !method.doUnfoldRequest()) {
            return Stream.concat(
                    Stream.of(ParameterSpec.builder(
                            requestTypeModel.getTypeName(),
                            "request"
                    ).build()),
                    generateObserverParameter().stream()
            ).toList();
        }
        return Stream.concat(
                method.getInputType().getFields().stream()
                        .map(f -> new FieldGenerator(context, f).generate())
                        .filter(FieldProcessingResult::isNonEmpty)
                        .map(FieldProcessingResult::field)
                        .map(Poem::fieldToParameter),
                generateObserverParameter().stream()
        ).toList();
    }
}
