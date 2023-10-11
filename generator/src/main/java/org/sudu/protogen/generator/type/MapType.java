package org.sudu.protogen.generator.type;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.utils.Name;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapType extends TypeModel {

    private final TypeModel keyModel;

    private final TypeModel valueModel;

    public MapType(TypeModel keyType, TypeModel valueType) {
        super(ParameterizedTypeName.get(
                ClassName.get(Map.class),
                keyType.getTypeName().box(),
                valueType.getTypeName().box()
        ));
        this.keyModel = keyType;
        this.valueModel = valueType;
    }

    public TypeModel getKeyModel() {
        return keyModel;
    }

    public TypeModel getValueModel() {
        return valueModel;
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        if (!(keyModel instanceof PrimitiveTypeModel) || !(valueModel instanceof PrimitiveTypeModel)) {
            CodeBlock keyMapper = buildToMapper(keyModel, "getKey", usedDefinitions);
            CodeBlock valueMapper = buildToMapper(valueModel, "getValue", usedDefinitions);
            return mapMapper(expr, keyMapper, valueMapper);
        }
        return expr;
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        if (!(keyModel instanceof PrimitiveTypeModel) || !(valueModel instanceof PrimitiveTypeModel)) {
            CodeBlock keyMapper = buildFromMapper(keyModel, "getKey", usedDefinitions);
            CodeBlock valueMapper = buildFromMapper(valueModel, "getValue", usedDefinitions);
            return mapMapper(expr, keyMapper, valueMapper);
        }
        return expr;
    }

    @NotNull
    private CodeBlock buildToMapper(TypeModel valueModel, String entryGetter, Set<String> usedDefinitions) {
        CodeBlock valueMapper = null;
        if (!(valueModel instanceof PrimitiveTypeModel)) {
            String nextDefinition = nextDefinition(usedDefinitions);
            CodeBlock lambdaParameter = CodeBlock.of(nextDefinition);
            Set<String> newDefinitions = new HashSet<>(usedDefinitions) {{ add(nextDefinition); }};

            CodeBlock valueGetter = CodeBlock.builder().add("$L.$L()", lambdaParameter, entryGetter).build();
            valueMapper = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, valueModel.toGrpcTransformer(valueGetter, newDefinitions))
                    .build();
        } else {
            valueMapper = CodeBlock.builder().add("$T::$L", Map.Entry.class, entryGetter).build();
        }
        return valueMapper;
    }

    @NotNull
    private CodeBlock buildFromMapper(TypeModel valueModel, String entryGetter, Set<String> usedDefinitions) {
        CodeBlock valueMapper = null;
        if (!(valueModel instanceof PrimitiveTypeModel)) {
            String nextDefinition = nextDefinition(usedDefinitions);
            CodeBlock lambdaParameter = CodeBlock.of(nextDefinition);
            Set<String> newDefinitions = new HashSet<>(usedDefinitions) {{ add(nextDefinition); }};

            CodeBlock valueGetter = CodeBlock.builder().add("$L.$L()", lambdaParameter, entryGetter).build();
            valueMapper = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, valueModel.fromGrpcTransformer(valueGetter, newDefinitions))
                    .build();
        } else {
            valueMapper = CodeBlock.builder().add("$T::$L", Map.Entry.class, entryGetter).build();
        }
        return valueMapper;
    }

    private CodeBlock mapMapper(CodeBlock expr, CodeBlock keyMapper, CodeBlock valueMapper) {
        return CodeBlock.builder()
                .add("$L.entrySet().stream()\n", expr)
                .indent()
                .add(".collect($T.toMap($L, $L))", Collectors.class, keyMapper, valueMapper)
                .unindent()
                .build();
    }

    @Override
    public String getterMethod(String protoFieldName) {
        return "get" + Name.toCamelCase(protoFieldName) + "Map";
    }

    @Override
    public String setterMethod(String protoFieldName) {
        return "putAll" + Name.toCamelCase(protoFieldName);
    }
}
