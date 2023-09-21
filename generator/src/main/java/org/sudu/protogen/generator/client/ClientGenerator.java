package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.descriptors.Service;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.util.stream.Stream;

public class ClientGenerator {

    private final GenerationContext context;

    private final Service service;

    private final FieldSpec stubField;

    public ClientGenerator(GenerationContext context, Service service) {
        this.context = context;
        this.service = service;
        this.stubField = FieldSpec.builder(service.blockingStubClass(), "blockingStub", Modifier.PROTECTED, Modifier.FINAL).build();
    }

    public TypeSpec generate() {
        CodeBlock constructorsBody = CodeBlock.builder()
                .addStatement("this.$N = $T.newBlockingStub($N)", stubField, service.stubClass(), BaseGrpcClient.channel)
                .build();

        TypeSpec.Builder builder = TypeSpec.classBuilder(service.generatedClientName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(BaseGrpcClient.clazz)
                .addField(stubField)
                .addAnnotation(AnnotationSpec.builder(ClassName.get(Generated.class)).addMember("value", CodeBlock.of("\"protogen\"")).build())
                .addMethods(BaseGrpcClient.generateConstructors(constructorsBody))
                .addMethods(service.getMethods().stream()
                        .filter(Method::doGenerate)
                        .flatMap(this::generateRpcMethod)
                        .toList()
                );
        if (service.isAbstract()) {
            builder.addModifiers(Modifier.ABSTRACT);
        } else {
            builder.addJavadoc(CodeBlock.of(BaseGrpcClient.modificationNotice));
        }
        return builder.build();
    }

    private Stream<MethodSpec> generateRpcMethod(Method method) {
        TypeModel returnType = getReturnType(method);
        TypeModel requestType = context.processType(method.getInputType());
        MethodSpec publicApi = new ApiMethodGeneratorBase(context, method, returnType, requestType).generate();
        MethodSpec grpcRequestMethod = new StubCallMethodGenerator(context, method, returnType, stubField).generate();
        return Stream.of(grpcRequestMethod, publicApi);
    }

    protected TypeModel getReturnType(Method method) {
        var responseType = context.processType(method.getOutputType());
        TypeModel type;
        if (responseType != null) {
            type = responseType;
        } else if (method.doUnfoldResponse(responseType)) {
            type = new UnfoldedType(context.processType(method.unfoldedResponseField()), method.getOutputType());
        } else {
            if (method.getOutputType().getFields().isEmpty()) {
                type = new TypeModel(TypeName.VOID);
            } else {
                throw new IllegalStateException(("Unable to create a method returning %s because request consist of more than " +
                        "1 field and doesn't have a domain object.").formatted(method.getOutputType().getFullName()));
            }
        }
        if (method.isOutputStreaming()) {
//            // todo research why
            if (type.getTypeName().toString().equalsIgnoreCase("void")) {
                return type;
            }
            return new RepeatedType(type, method.getStreamToContainer());
        }
        return type;
    }

}
