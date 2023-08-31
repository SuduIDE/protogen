package org.sudu.protogen.generator.type;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.utils.Name;

import java.util.Map;
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
    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        if (keyModel instanceof DomainType || valueModel instanceof DomainType || keyModel instanceof UnfoldedType || valueModel instanceof UnfoldedType) {
            CodeBlock keyMapper = buildToMapper(keyModel, "getKey");
            CodeBlock valueMapper = buildToMapper(valueModel, "getValue");
            return mapMapper(expr, keyMapper, valueMapper);
        }
        return expr;
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        if (keyModel instanceof DomainType || valueModel instanceof DomainType || keyModel instanceof UnfoldedType || valueModel instanceof UnfoldedType) {
            CodeBlock keyMapper = buildFromMapper(keyModel, "getKey");
            CodeBlock valueMapper = buildFromMapper(valueModel, "getValue");
            return mapMapper(expr, keyMapper, valueMapper);
        }
        return expr;
    }

    @NotNull
    private CodeBlock buildToMapper(TypeModel valueModel, String entryGetter) {
        CodeBlock lambdaParameter = CodeBlock.builder().add("i").build();
        CodeBlock valueMapper = null;
        if (valueModel instanceof DomainType || valueModel instanceof UnfoldedType) {
            CodeBlock valueGetter = CodeBlock.builder().add("$L.$L()", lambdaParameter, entryGetter).build();
            valueMapper = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, valueModel.toGrpcTransformer(valueGetter))
                    .build();
        } else {
            valueMapper = CodeBlock.builder().add("$T::$L", Map.Entry.class, entryGetter).build();
        }
        return valueMapper;
    }

    @NotNull
    private CodeBlock buildFromMapper(TypeModel valueModel, String entryGetter) {
        CodeBlock lambdaParameter = CodeBlock.builder().add("i").build();
        CodeBlock valueMapper = null;
        if (valueModel instanceof DomainType || valueModel instanceof UnfoldedType) {
            CodeBlock valueGetter = CodeBlock.builder().add("$L.$L()", lambdaParameter, entryGetter).build();
            valueMapper = CodeBlock.builder()
                    .add("$L -> $L", lambdaParameter, valueModel.fromGrpcTransformer(valueGetter))
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
