package org.sudu.protogen.generator.enumeration;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Enum;
import org.sudu.protogen.generator.GenerationContext;

import javax.lang.model.element.Modifier;

public class EnumGenerator {

    private final GenerationContext context;
    private final Enum anEnum;
    private final TypeName protoType;
    private final TypeName generatedType;

    public EnumGenerator(GenerationContext context, Enum anEnum) {
        this.context = context;
        this.anEnum = anEnum;
        this.protoType = anEnum.getProtobufTypeName(context.configuration().namingManager());
        this.generatedType = anEnum.getGeneratedTypeName(context.configuration().namingManager());
    }

    public TypeSpec generate() {
        var builder = TypeSpec.enumBuilder(anEnum.generatedName(context.configuration().namingManager()))
                .addModifiers(Modifier.PUBLIC);
        anEnum.getValues().stream()
                .filter(val -> !val.isUnused())
                .map(this::getNameForValue)
                .forEach(builder::addEnumConstant);
        builder.addMethod(generateToGrpcMethod());
        builder.addMethod(generateFromGrpcMethod());
        return builder.build();
    }

    @NotNull
    private String getNameForValue(Enum.Value descriptor) {
        return descriptor.generatedName();
    }

    private MethodSpec generateToGrpcMethod() {
        CodeBlock.Builder switchBlock = CodeBlock.builder().add("switch (this) {$>\n");
        for (var value : anEnum.getValues()) {
            if (value.isUnused()) continue;
            switchBlock.add("case $L -> $T.$L;\n", getNameForValue(value), protoType, value.getName());
        }
        switchBlock.add("$<}");

        return MethodSpec.methodBuilder("toGrpc")
                .addModifiers(Modifier.PUBLIC)
                .returns(protoType)
                .addStatement("return $L", switchBlock.build())
                .build();
    }

    private MethodSpec generateFromGrpcMethod() {
        CodeBlock.Builder switchBlock = CodeBlock.builder().add("switch (grpc) {$>\n");
        for (var value : anEnum.getValues()) {
            if (value.isUnused()) {
                switchBlock.add("case $L -> throw new $T(\"Enum value $L is marked as unused!\");\n", value.getName(), IllegalArgumentException.class, getNameForValue(value));
            } else {
                switchBlock.add("case $L -> $T.$L;\n", value.getName(), generatedType, getNameForValue(value));
            }
        }
        switchBlock.add("case UNRECOGNIZED -> throw new $T($S);\n", IllegalArgumentException.class, "Enum value is not recognized");
        switchBlock.add("$<}");
        return MethodSpec.methodBuilder("fromGrpc")
                .returns(generatedType)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(protoType, "grpc").build())
                .addStatement("return $L", switchBlock.build())
                .build();
    }
}
