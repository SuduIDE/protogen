package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.RepeatedContainer;
import org.sudu.protogen.utils.Name;

public class RepeatedType extends TypeModel {

    private final TypeModel elementModel;

    private final RepeatedContainer repeatedType;

    public RepeatedType(TypeModel typeName, RepeatedContainer repeatedType) {
        super(ParameterizedTypeName.get(
                repeatedType.getTypeName(),
                typeName.getTypeName().box()
        ));
        this.elementModel = typeName;
        this.repeatedType = repeatedType;
    }

    public TypeModel getElementModel() {
        return elementModel;
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        if (!(elementModel instanceof PrimitiveTypeModel)) {
            CodeBlock lambdaParameter = CodeBlock.of("i");
            CodeBlock mappingLambda = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, elementModel.toGrpcTransformer(lambdaParameter))
                    .build();
            expr = listMapper(expr, mappingLambda);
        }
        return repeatedType.convertInstanceToIterable(expr);
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        expr = repeatedType.convertListToInstance(expr);
        if (!(elementModel instanceof PrimitiveTypeModel)) {
            CodeBlock lambdaParameter = CodeBlock.builder().add("i").build();
            CodeBlock mappingLambda = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, elementModel.fromGrpcTransformer(lambdaParameter))
                    .build();
            return listMapper(expr, mappingLambda);
        }
        return expr;
    }

    @NotNull
    private CodeBlock listMapper(CodeBlock expr, CodeBlock mapper) {
        return CodeBlock.builder()
                .add("$L\n", repeatedType.getToStreamExpr(expr))
                .indent()
                .add(".map($L)\n", mapper)
                .add("$L", repeatedType.getCollectorExpr())
                .unindent()
                .build();
    }

    @Override
    public String getterMethod(String protoFieldName) {
        return "get" + Name.toCamelCase(protoFieldName) + "List";
    }

    @Override
    public String setterMethod(String protoFieldName) {
        return "addAll" + Name.toCamelCase(protoFieldName);
    }
}
