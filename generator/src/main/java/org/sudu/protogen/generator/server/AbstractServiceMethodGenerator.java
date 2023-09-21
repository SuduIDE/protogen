package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.Objects;
import java.util.stream.Stream;

public class AbstractServiceMethodGenerator {

    private final GenerationContext context;

    private final Method method;

    private final @Nullable TypeModel requestType;

    private final @Nullable TypeModel responseType;

    public AbstractServiceMethodGenerator(GenerationContext context, Method method) {
        this.context = context;
        this.method = method;
        this.requestType = context.processType(method.getInputType());
        this.responseType = context.processType(method.getOutputType());
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.generatedName())
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .addParameters(generateMethodParameters())
                .build();
    }

    /**
     * Generates a parameter for response observer or returns null if it is not required
     */
    @Nullable
    private ParameterSpec generateObserverParameter() {
        if (responseType != null && responseType.getTypeName() == TypeName.VOID) {
            return null;
        }
        if (method.getOutputType().getFields().isEmpty()) {
            return null;
        }
        TypeName type = ParameterizedTypeName.get(
                ClassName.get("io.grpc.stub", "StreamObserver"),
                responseType().box() // as a processed type could be a primitive, always box it
        );
        return ParameterSpec.builder(type, "responseObserver").build();
    }

    /**
     * If the return type is processed by a type processor, returns it
     * If the response message is one-field, unfolds it
     * Otherwise generates protoc-generated class
     */
    private TypeName responseType() {
        if (responseType != null) {
            return responseType.getTypeName();
        }
        if (method.doUnfoldResponse(responseType)) {
            var field = method.unfoldedResponseField();
            return context.processType(field).getTypeName();
        }
        return method.getOutputType().getProtobufTypeName();
    }

    private Iterable<ParameterSpec> generateMethodParameters() {
        if (requestType == null || method.doUnfoldRequest()) {
            Stream<ParameterSpec> unfoldedFields = FieldGenerator.generateSeveral(method.getInputType().getFields(), context)
                    .map(FieldProcessingResult::field)
                    .map(Poem::fieldToParameter);
            return StreamEx.of(unfoldedFields).append(generateObserverParameter()).nonNull().toList();
        }
        if (requestType.getTypeName() == TypeName.VOID) {
            return Stream.of(generateObserverParameter()).filter(Objects::nonNull).toList();
        }
        return Stream.of(
                ParameterSpec.builder(requestType.getTypeName(), "request").build(),
                generateObserverParameter()
        ).filter(Objects::nonNull).toList();
    }
}
