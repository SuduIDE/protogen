package org.sudu.protogen.generator.message;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.DescriptorGenerator;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.utils.Name;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;

public class MessageGenerator implements DescriptorGenerator<Message, TypeSpec> {

    private final GenerationContext generationContext;

    public MessageGenerator(@NotNull GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    @NotNull
    public TypeSpec generate(@NotNull Message msgDescriptor) {
        TypeSpec.Builder typeBuilder = TypeSpec.recordBuilder(generatedType(msgDescriptor).simpleName());

        List<FieldProcessingResult> processedFields = msgDescriptor.getFields().stream()
                .map(field -> generationContext.generatorsHolder().generate(field))
                .filter(FieldProcessingResult::isNonVoid)
                .toList();

        addComponents(processedFields, typeBuilder);
        implementComparable(msgDescriptor, typeBuilder);
        addTopicField(msgDescriptor, typeBuilder);
        addTransformingMethods(msgDescriptor, processedFields, typeBuilder);
        addOneofs(msgDescriptor, typeBuilder);

        return typeBuilder
                .multiLineRecord(true)
                .addModifiers(Modifier.PUBLIC)
                .addTypes(generateNested(msgDescriptor))
                .build();
    }

    private void addOneofs(Message msgDescriptor, TypeSpec.Builder typeBuilder) {
        msgDescriptor.getOneofs().forEach(oneOf -> {
            if (oneOf.getFieldsCases().size() < 2) return;
            String oneOfName = Name.toCamelCase(oneOf.getName()) + "Case";
            ClassName domainTypeName = oneOf.getDomainTypeName(generationContext.configuration().namingManager());

            TypeSpec.Builder oneOfSpecBuilder = TypeSpec.enumBuilder(oneOfName).shortEnumNotation(true);
            oneOf.getFieldsCases().forEach(oneOfSpecBuilder::addEnumConstant);
            oneOfSpecBuilder.addEnumConstant("NOT_SET");
            oneOfSpecBuilder.addMethod(MethodSpec.methodBuilder("fromProto")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ParameterSpec.builder(oneOf.getProtobufTypeName(), "proto").build())
                    .addStatement("""
                            return switch(proto) {$>
                            $L
                            case $L -> NOT_SET;
                            $<}""",
                            oneOf.getFieldsCases().stream()
                                    .map(c -> CodeBlock.of("case $L -> $L;", c, c))
                                    .collect(Poem.joinCodeBlocks("\n")),
                            oneOf.getName().toUpperCase() + "_NOT_SET"
                    )
                    .returns(domainTypeName)
                    .build()
            );
            TypeSpec oneOfSpec = oneOfSpecBuilder.build();
            typeBuilder.addType(oneOfSpec);

            typeBuilder.addMethod(MethodSpec.methodBuilder("get" + oneOfName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(domainTypeName)
                    .addStatement("return $T.fromProto(toGrpc().get$L())", domainTypeName, oneOfName)
                    .build()
            );
        });
    }

    private void addComponents(List<FieldProcessingResult> processedFields, TypeSpec.Builder typeBuilder) {
        List<ParameterSpec> parameters = processedFields.stream()
                .map(FieldProcessingResult::field)
                .map(Poem::fieldToParameter)
                .toList();
        typeBuilder.addRecordComponents(parameters);
    }

    private void implementComparable(@NotNull Message msgDescriptor, TypeSpec.Builder typeBuilder) {
        msgDescriptor.getComparatorReference().ifPresent(comparator -> {
            ClassName type = generatedType(msgDescriptor);
            typeBuilder.addSuperinterface(ParameterizedTypeName.get(ClassName.get(Comparable.class), type));
            ParameterSpec methodParameter = ParameterSpec.builder(type, "rhs").build();
            typeBuilder.addMethod(
                    MethodSpec.methodBuilder("compareTo")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.INT)
                            .addParameter(methodParameter)
                            .addAnnotation(ClassName.get(Override.class))
                            .addStatement("return $L.compare(this, $N)", comparator, methodParameter)
                            .build()
            );
        });
    }

    private void addTopicField(@NotNull Message msgDescriptor, TypeSpec.Builder typeBuilder) {
        msgDescriptor.getTopic().ifPresent(topic -> typeBuilder.addField(
                FieldSpec.builder(ClassName.get(String.class), "TOPIC", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer(CodeBlock.of("\"$L\"", topic)).build()
        ));
    }

    private void addTransformingMethods(@NotNull Message msgDescriptor, List<FieldProcessingResult> processedFields, TypeSpec.Builder typeBuilder) {
        boolean annotateNotNull = msgDescriptor.getContainingFile().doUseNullabilityAnnotation(false);
        typeBuilder
                .addMethod(new FromGrpcMethodGenerator(
                        generationContext,
                        generatedType(msgDescriptor),
                        protoType(msgDescriptor),
                        processedFields,
                        annotateNotNull
                ).generate())
                .addMethod(new ToGrpcMethodGenerator(
                        generationContext,
                        protoType(msgDescriptor),
                        processedFields,
                        annotateNotNull
                ).generate());
    }

    private List<TypeSpec> generateNested(@NotNull Message msgDescriptor) {
        return msgDescriptor.getNested().stream()
                .filter(EnumOrMessage::doGenerate)
                .map(e -> generationContext.generatorsHolder().generate(e))
                .toList();
    }

    private ClassName protoType(@NotNull Message msgDescriptor) {
        return msgDescriptor.getProtobufTypeName();
    }

    private ClassName generatedType(@NotNull Message msgDescriptor) {
        return msgDescriptor.getDomainTypeName(generationContext.configuration().namingManager());
    }
}
