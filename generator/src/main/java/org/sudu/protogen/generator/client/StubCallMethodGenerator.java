package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.sudu.protogen.BaseClientUtils;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.descriptors.RepeatedContainer;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;
import protogen.Options;

import javax.lang.model.element.Modifier;
import java.util.List;

public class StubCallMethodGenerator {

    protected final FieldSpec stubField;

    private final GenerationContext context;

    private final Method method;

    private final TypeModel returnType;

    public StubCallMethodGenerator(GenerationContext context, Method method, TypeModel returnType, FieldSpec stubField) {
        this.context = context;
        this.method = method;
        this.returnType = returnType;
        this.stubField = stubField;
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.generatedName() + "StubCall")
                .addModifiers(Modifier.PRIVATE)
                .addParameters(parameters())
                .addCode(body())
                .returns(returnType.getTypeName())
                .addAnnotation(
                        method.ifNotFoundBehavior() == Options.IfNotFound.NULLIFY
                                ? context.configuration().nullableAnnotationClass()
                                : context.configuration().nonnullAnnotationClass()
                )
                .build();
    }

    private CodeBlock body() {
        CodeBlock returnExpr = CodeBlock.of("$N.$L(request)", stubField, method.getName());
        CodeBlock body = CodeBlock.of("");

        if (returnType instanceof RepeatedType repType) { // i.e. method is streaming non-void
            body = CodeBlock.builder()
                    .addStatement(
                            "var iterator = $L",
                            returnExpr
                    )
                    .build();
            // I write mapping here manually because input is always an Iterator<Grpc..> and output is specified by the RepeatedContainer option
            // So RepeatedType.fromGrpcTransformer is not suitable because it does only T<U> <--> T<V> mappings
            CodeBlock mappingExpr = CodeBlock.of("i -> $L", repType.getElementModel().fromGrpcTransformer(CodeBlock.of("i")));
            returnExpr = CodeBlock.of("$L\n.map($L)$L", RepeatedContainer.ITERATOR.getToStreamExpr(CodeBlock.of("iterator")), mappingExpr, repType.getRepeatedType().getCollectorExpr());
        } else {
            // Don't transform if returnType is streaming (RepeatedType), see the comment above
            returnExpr = returnType.fromGrpcTransformer(returnExpr);
        }

        switch (method.ifNotFoundBehavior()) {
            case NULLIFY ->
                    returnExpr = CodeBlock.of("$T.nullifyIfNotFound(() -> $L)", BaseClientUtils.class, returnExpr);
            case EMPTY -> returnExpr = CodeBlock.of("$T.emptyIfNotFound(() -> $L)", BaseClientUtils.class, returnExpr);
        }

        if (returnType.getTypeName() != TypeName.VOID) {
            returnExpr = CodeBlock.of("return $L", returnExpr);
        }
        return CodeBlock.builder().add(body).addStatement(returnExpr).build();
    }

    private List<ParameterSpec> parameters() {
        ClassName protoType = method.getInputType().getProtobufTypeName();
        return List.of(ParameterSpec.builder(protoType, "request").build());
    }
}
