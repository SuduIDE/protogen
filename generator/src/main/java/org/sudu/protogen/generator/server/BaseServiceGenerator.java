package org.sudu.protogen.generator.server;

import com.squareup.javapoet.*;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.descriptors.Service;
import org.sudu.protogen.generator.GenerationContext;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.util.stream.Stream;

public class BaseServiceGenerator {

    private final GenerationContext context;

    private final Service service;

    public BaseServiceGenerator(GenerationContext context, Service service) {
        this.context = context;
        this.service = service;
    }

    public TypeSpec generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(service.generatedServiceName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(superclass())
                .addAnnotation(AnnotationSpec.builder(ClassName.get(Generated.class)).addMember("value", CodeBlock.of("\"protogen\"")).build())
                .addMethods(methods());

        return builder.build();
    }

    private Iterable<MethodSpec> methods() {
        return service.getMethods().stream()
                .filter(Method::doGenerate)
                .flatMap(method -> Stream.of(
                        new AbstractServiceMethodGenerator(context, method).generate(),
                        new OverriddenServiceMethodGenerator(context, method).generate()
                ))
                .toList();
    }

    private TypeName superclass() {
        return ClassName.get(
                service.getContainingFile().getProtoPackage(),
                service.getName() + "Grpc",
                service.getName() + "ImplBase"
        );
    }
}
