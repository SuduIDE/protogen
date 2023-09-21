package org.sudu.protogen.generator.client;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.Validate;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public class DomainRequestMethodBuilder extends MethodGeneratorBase {

    public DomainRequestMethodBuilder(GenerationContext context, Method method, FieldSpec stubField) {
        super(context, method, stubField);
        Validate.validState(requestType != null);
    }

    @Override
    protected List<ParameterSpec> parameters() {
        if (requestType.getTypeName() == TypeName.VOID) {
            return List.of();
        }
        return List.of(ParameterSpec.builder(requestType.getTypeName(), "request").build());
    }

    @Override
    protected CodeBlock body(TypeModel returnType, List<ParameterSpec> params) {
        return CodeBlock.of("");
    }

    @Override
    protected CodeBlock returnExpression(TypeModel returnType, List<ParameterSpec> params) {
        if (params.isEmpty()) {
            return CodeBlock.of("$L($L)", method.generatedName(), requestType.toGrpcTransformer(CodeBlock.of("")));
        }
        ParameterSpec requestParam = params.get(0);
        return CodeBlock.of("$L($L)", method.generatedName(), requestType.toGrpcTransformer(CodeBlock.of("$N", requestParam)));
    }
}
