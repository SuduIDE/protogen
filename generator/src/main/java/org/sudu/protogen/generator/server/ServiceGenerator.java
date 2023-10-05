package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.descriptors.Service;
import org.sudu.protogen.generator.DescriptorGenerator;
import org.sudu.protogen.generator.GenerationContext;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.util.stream.Stream;

public class ServiceGenerator implements DescriptorGenerator<Service, TypeSpec> {

    private final GenerationContext context;

    public ServiceGenerator(GenerationContext context) {
        this.context = context;
    }

    @Override
    public TypeSpec generate(Service service) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(service.generatedServiceName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(protobufStubType(service))
                .addAnnotation(AnnotationSpec.builder(ClassName.get(Generated.class)).addMember("value", CodeBlock.of("\"protogen\"")).build())
                .addMethods(methods(service));

        return builder.build();
    }

    private Iterable<MethodSpec> methods(Service service) {
        return service.getMethods().stream()
                .filter(Method::doGenerate)
                .flatMap(method -> {
                    MethodSpec abstractMethod = new AbstractServiceMethodGenerator(context, method).generate();
                    MethodSpec overriddenMethod = new OverriddenServiceMethodGenerator(context, method, abstractMethod).generate();
                    return Stream.of(abstractMethod, overriddenMethod);
                })
                .toList();
    }

    private TypeName protobufStubType(Service service) {
        return ClassName.get(
                service.getContainingFile().getProtoPackage(),
                service.getName() + "Grpc",
                service.getName() + "ImplBase"
        );
    }
}
