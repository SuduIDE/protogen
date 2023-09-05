package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.protobuf.RepeatedContainer;
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
        if (elementModel instanceof DomainType || elementModel instanceof UnfoldedType) {
            CodeBlock lambdaParameter = CodeBlock.of("i");
            CodeBlock mappingLambda = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, elementModel.toGrpcTransformer(lambdaParameter))
                    .build();
            return listMapper(expr, mappingLambda);
        }
        return expr;
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        if (elementModel instanceof DomainType || elementModel instanceof UnfoldedType || repeatedType != RepeatedContainer.LIST) {
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
                .add("$L.stream()\n", expr)
                .indent()
                .add(".map($L)\n", mapper)
                .add(".collect($L)", repeatedType.getCollectorExpr())
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
