package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.type.IteratorType;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public abstract class MethodGeneratorBase {

    protected final GenerationContext context;

    protected final Method method;

    protected final FieldSpec stubField;

    public MethodGeneratorBase(GenerationContext context, Method method, FieldSpec stubField) {
        this.context = context;
        this.method = method;
        this.stubField = stubField;
    }

    protected abstract List<ParameterSpec> parameters();

    protected abstract CodeBlock body(TypeModel returnType, List<ParameterSpec> params);

    protected abstract CodeBlock returnExpression(TypeModel returnType, List<ParameterSpec> params);

    public MethodSpec generate() {

        TypeModel returnType = getReturnType();
        List<ParameterSpec> params = parameters();
        CodeBlock body = body(returnType, params);
        CodeBlock returnExpr = returnExpression(returnType, params);

        return MethodSpec.methodBuilder(method.generatedName())
                .addParameters(params)
                .returns(returnType.getTypeName())
                .addAnnotation(
                        method.isNullable()
                                ? context.configuration().nullableAnnotationClass()
                                : context.configuration().nonnullAnnotationClass()
                )
                .addCode(body)
                .addStatement(returnStatement(returnType.getTypeName(), returnExpr))
                .build();
    }

    private CodeBlock returnStatement(TypeName returnType, CodeBlock expr) {
        // .equals(TypeName.Void) isn't suitable because ClassName("void") isn't equal to TypeName.Void
        if (returnType.toString().equalsIgnoreCase("void")) {
            return expr;
        }
        return CodeBlock.builder().add("return $L", expr).build();
    }

    protected TypeModel getReturnType() {
        TypeModel type;
        if (method.doUnfoldResponse()) {
            Field field = method.getOutputType().getFields().get(0);
            type = new FieldGenerator(context, field).generate().type();
        } else if (method.getOutputType().isDomain()) {
            type = context.typeProcessor().processType(method.getOutputType(), context);
        } else {
            if (method.getOutputType().getFields().isEmpty()) {
                type = new TypeModel(TypeName.VOID);
            } else {
                throw new IllegalStateException(("Unable to create a method returning %s because request consist of more than " +
                        "1 field and doesn't have a domain object.").formatted(method.getOutputType().getFullName()));
            }
        }
        if (method.isOutputStreaming()) {
            if (type.getTypeName().toString().equalsIgnoreCase("void")) {
                return type;
            }
            var containerO = method.getStreamToContainer();
            if (containerO.isPresent()) {
                return new RepeatedType(type, containerO.get());
            } else {
                return new IteratorType(type);
            }
        }
        return type;
    }
}
