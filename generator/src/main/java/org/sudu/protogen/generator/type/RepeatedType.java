package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.RepeatedContainer;
import org.sudu.protogen.utils.Name;

import java.util.HashSet;
import java.util.Set;

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

    public RepeatedContainer getRepeatedType() {
        return repeatedType;
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        if (!(elementModel instanceof PrimitiveTypeModel && repeatedType == RepeatedContainer.LIST)) {
            String nextDefinition = nextDefinition(usedDefinitions);
            CodeBlock lambdaParameter = CodeBlock.of(nextDefinition);
            Set<String> newDefinitions = new HashSet<>(usedDefinitions) {{ add(nextDefinition); }};

            CodeBlock mappingLambda = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, elementModel.toGrpcTransformer(lambdaParameter, newDefinitions))
                    .build();
            expr = listMapper(expr, mappingLambda);
        }
        return repeatedType.convertInstanceToIterable(expr);
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        expr = repeatedType.convertListToInstance(expr);
        if (!(elementModel instanceof PrimitiveTypeModel && repeatedType == RepeatedContainer.LIST)) {
            String nextDefinition = nextDefinition(usedDefinitions);
            CodeBlock lambdaParameter = CodeBlock.of(nextDefinition);
            Set<String> newDefinitions = new HashSet<>(usedDefinitions) {{ add(nextDefinition); }};

            CodeBlock mappingLambda = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, elementModel.fromGrpcTransformer(lambdaParameter, newDefinitions))
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
