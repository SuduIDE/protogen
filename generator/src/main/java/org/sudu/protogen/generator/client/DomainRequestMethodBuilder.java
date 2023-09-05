package org.sudu.protogen.generator.client;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public class DomainRequestMethodBuilder extends MethodGeneratorBase {

    public DomainRequestMethodBuilder(GenerationContext context, Method method, FieldSpec stubField) {
        super(context, method, stubField);
    }

    @Override
    protected List<ParameterSpec> parameters() {
        ClassName domainType = context.domainTypeTable().getType(method.getInputType());
        return List.of(ParameterSpec.builder(domainType, "request").build());
    }

    @Override
    protected CodeBlock body(TypeModel returnType, List<ParameterSpec> params) {
        return CodeBlock.of("");
    }

    @Override
    protected CodeBlock returnExpression(TypeModel returnType, List<ParameterSpec> params) {
        ParameterSpec requestParam = params.get(0);
        return CodeBlock.of("$L($N.toGrpc())", method.generatedName(), requestParam);
    }
}
