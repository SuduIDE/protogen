package org.sudu.protogen.generator.message;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.DescriptorGenerator;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerationHelper;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.utils.Name;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.function.Predicate;

public class MessageBuilderGenerator implements DescriptorGenerator<Message, TypeSpec> {

    private final GenerationContext context;

    public MessageBuilderGenerator(@NotNull GenerationContext context) {
        this.context = context;
    }

    @Override
    public TypeSpec generate(Message descriptor) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(getBuilderName(descriptor))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        addFields(descriptor, builder);
        addConstructor(descriptor, builder);
        addSetters(descriptor, builder);
        addBuildMethod(descriptor, builder);
        return builder.build();
    }

    private void addBuildMethod(Message descriptor, TypeSpec.Builder builder) {
        ClassName messageType = descriptor.getDomainTypeName(context.configuration().namingManager());
        MethodSpec buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NotNull.class)
                .returns(messageType)
                .addStatement("return new $T($L)", messageType,
                        Poem.separatedSequence(
                                FieldGenerationHelper.processAllFields(descriptor, context)
                                        .map(FieldProcessingResult::field)
                                        .map(field -> CodeBlock.of("$N", field))
                                        .toList(),
                                ", "
                        ))
                .build();
        builder.addMethod(buildMethod);
    }

    private void addSetters(Message descriptor, TypeSpec.Builder builder) {
        String builderName = getBuilderName(descriptor);
        FieldGenerationHelper.processAllFields(descriptor, context)
                .filter(FieldProcessingResult::isNullable)
                .map(FieldProcessingResult::field)
                .map(field -> setterForField(field, builderName))
                .forEach(builder::addMethod);
    }

    @NotNull
    private MethodSpec setterForField(FieldSpec fieldSpec, String builderName) {
        return MethodSpec.methodBuilder("set" + Name.toCamelCase(fieldSpec.name))
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("",builderName))
                .addParameter(Poem.fieldToParameter(fieldSpec))
                .addStatement("this.$N = $N", fieldSpec, fieldSpec)
                .addStatement("return this")
                .build();
    }

    private void addConstructor(Message descriptor, TypeSpec.Builder builder) {
        List<FieldSpec> nonNullFields = FieldGenerationHelper.processAllFields(descriptor, context)
                .filter(Predicate.not(FieldProcessingResult::isNullable))
                .map(FieldProcessingResult::field)
                .map(field -> field.toBuilder().clearAnnotations().build())
                .toList();
        MethodSpec constructor = MethodSpec.constructorBuilder().addParameters(
                nonNullFields.stream()
                        .map(Poem::fieldToParameter)
                        .toList()
        ).addModifiers(Modifier.PRIVATE).addCode(
                nonNullFields.stream()
                        .map(field -> CodeBlock.of("this.$N = $N;", field, field))
                        .collect(Poem.joinCodeBlocks("\n"))
        ).build();
        builder.addMethod(constructor);
    }

    private void addFields(Message descriptor, TypeSpec.Builder builder) {
        FieldGenerationHelper.processAllFields(descriptor, context)
                .map(FieldProcessingResult::field)
                .map(field -> field.toBuilder().clearAnnotations().addModifiers(Modifier.PRIVATE).build())
                .forEach(builder::addField);
    }

    @NotNull
    private String getBuilderName(Message descriptor) {
        return descriptor.getDomainTypeName(context.configuration().namingManager()).simpleName() + "Builder";
    }

}
