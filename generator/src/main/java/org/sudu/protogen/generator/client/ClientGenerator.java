package org.sudu.protogen.generator.client;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.descriptors.Service;
import org.sudu.protogen.generator.GenerationContext;

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

        TypeSpec.Builder builder = TypeSpec.classBuilder(service.generatedName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(BaseGrpcClient.clazz)
                .addField(stubField)
                .addMethods(BaseGrpcClient.generateConstructors(constructorsBody))
                .addMethods(service.getMethods().stream()
                        .filter(Method::doGenerate)
                        .flatMap(this::generateRpcMethod)
                        .toList()
                );
        if (service.isAbstract()) {
            builder.addModifiers(Modifier.ABSTRACT);
        }
        return builder.build();
    }

    private Stream<MethodSpec> generateRpcMethod(Method method) {
        MethodSpec publicApi = method.doUnfoldRequest()
                ? generateParamsListMethod(method)
                : generateWrappedRequestMethod(method);
        MethodSpec grpcRequestMethod = generateGrpcRequestMethod(method);
        return Stream.of(grpcRequestMethod, publicApi);
    }

    private MethodSpec generateWrappedRequestMethod(Method method) {
        return new DomainRequestMethodBuilder(context, method, stubField).generate()
                .toBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    private MethodSpec generateParamsListMethod(Method method) {
        return new UnfoldedRequestMethodGenerator(context, method, stubField).generate()
                .toBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    private MethodSpec generateGrpcRequestMethod(Method method) {
        if (method.isOutputStreaming()) {
            return new StreamingGrpcCallMethodGenerator(context, method, stubField).generate()
                    .toBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .build();
        } else {
            return new GrpcCallMethodGenerator(context, method, stubField).generate()
                    .toBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .build();
        }
    }

}
