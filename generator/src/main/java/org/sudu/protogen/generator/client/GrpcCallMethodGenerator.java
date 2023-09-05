package org.sudu.protogen.generator.client;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public class GrpcCallMethodGenerator extends MethodGeneratorBase {

    public GrpcCallMethodGenerator(GenerationContext context, Method method, FieldSpec stubField) {
        super(context, method, stubField);
    }

    @Override
    protected List<ParameterSpec> parameters() {
        ClassName protoType = context.protoTypeTable().getType(method.getInputType());
        return List.of(ParameterSpec.builder(protoType, "request").build());
    }

    @Override
    protected CodeBlock body(TypeModel returnType, List<ParameterSpec> params) {
        return CodeBlock.of("");
    }

    @Override
    protected CodeBlock returnExpression(TypeModel returnType, List<ParameterSpec> params) {
        CodeBlock returnExpr = onlyCallReturnExpression(returnType, params);

        if (method.doUnfoldResponse()) {
            Field field = method.getOutputType().getFields().get(0);
            returnExpr = CodeBlock.of("$L.$L()", returnExpr, returnType.getterMethod(field.getName()));
        }

        returnExpr = returnType.fromGrpcTransformer(returnExpr);
        if (method.isNullable()) {
            returnExpr = CodeBlock.of("nullifyIfNotFound(() -> $L)", returnExpr);
        }
        return returnExpr;
    }

    @NotNull
    protected CodeBlock onlyCallReturnExpression(TypeModel returnType, List<ParameterSpec> params) {
        ParameterSpec param = params.get(0);
        return CodeBlock.of("$N.$L($N)", stubField, method.getName(), param);
    }
}
