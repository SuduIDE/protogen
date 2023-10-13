package org.sudu.protogen.generator.enumeration;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Enum;
import org.sudu.protogen.generator.DescriptorGenerator;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;

public class EnumGenerator implements DescriptorGenerator<Enum, TypeSpec> {

    private final GenerationContext context;

    public EnumGenerator(GenerationContext context) {
        this.context = context;
    }

    public TypeSpec generate(Enum anEnum) {
        ClassName domainTypeName = anEnum.getDomainTypeName(context.configuration().namingManager());
        TypeSpec.Builder builder = TypeSpec.enumBuilder(domainTypeName.simpleName())
                .shortEnumNotation(true)
                .addModifiers(Modifier.PUBLIC);

        anEnum.getValues().stream()
                .filter(val -> !val.isUnused())
                .map(this::getNameForValue)
                .forEach(builder::addEnumConstant);

        addMethods(anEnum, builder);

        return builder.build();
    }

    private void addMethods(Enum anEnum, TypeSpec.Builder builder) {
        TypeName protoType = anEnum.getProtobufTypeName();
        TypeName generatedType = anEnum.getDomainTypeName(context.configuration().namingManager());
        builder.addMethod(generateToGrpcMethod(anEnum, protoType));
        builder.addMethod(generateFromGrpcMethod(anEnum, generatedType, protoType));
    }

    @NotNull
    private String getNameForValue(Enum.Value descriptor) {
        return descriptor.generatedName();
    }

    private MethodSpec generateToGrpcMethod(Enum anEnum, TypeName protoType) {
        CodeBlock switchCases = anEnum.getValues().stream()
                .map(value -> CodeBlock.of("case $L -> $T.$L;\n", getNameForValue(value), protoType, value.getName()))
                .collect(Poem.joinCodeBlocks());
        return MethodSpec.methodBuilder("toGrpc")
                .addModifiers(Modifier.PUBLIC)
                .returns(protoType)
                .addStatement("""
                        return switch (this) {$>
                        $L
                        $<}
                        """, switchCases
                ).build();
    }

    private MethodSpec generateFromGrpcMethod(Enum anEnum, TypeName generatedType, TypeName protoType) {
        CodeBlock switchCases = anEnum.getValues().stream()
                .map(value -> value.isUnused()
                        ? CodeBlock.of("case $L -> throw new $T(\"Enum value $L is marked as unused!\");\n", value.getName(), IllegalArgumentException.class, getNameForValue(value))
                        : CodeBlock.of("case $L -> $T.$L;\n", value.getName(), generatedType, getNameForValue(value))
                ).collect(Poem.joinCodeBlocks());
        switchCases = switchCases.toBuilder().add("case UNRECOGNIZED -> throw new $T($S);\n", IllegalArgumentException.class, "Enum value is not recognized").build();
        return MethodSpec.methodBuilder("fromGrpc")
                .returns(generatedType)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(protoType, "grpc").build())
                .addStatement("""
                        return switch (grpc) {$>
                        $L
                        $<}
                        """, switchCases
                ).build();
    }
}
