package org.sudu.protogen.generator.message;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.protobuf.Message;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;

public class MessageGenerator {

    private final GenerationContext generationContext;
    private final Message msgDescriptor;
    private final List<FieldProcessingResult> processedFields;

    public MessageGenerator(
            @NotNull GenerationContext generationContext,
            @NotNull Message msgDescriptor
    ) {
        this.generationContext = generationContext;
        this.msgDescriptor = msgDescriptor;
        this.processedFields = msgDescriptor.getFields().stream()
                .map(field -> new FieldGenerator(generationContext, field).generate())
                .filter(FieldProcessingResult::isNonEmpty)
                .toList();
    }

    @NotNull
    public TypeSpec generate() {
        List<FieldSpec> fields = processedFields.stream()
                .map(FieldProcessingResult::field)
                .toList();

        TypeSpec.Builder typeBuilder = getRecordBuilder(fields);

        boolean annotateNotNull = msgDescriptor.getContainingFile().doUseNullabilityAnnotation(false);

        return typeBuilder
                .multiLineRecord(true)
                .addModifiers(Modifier.PUBLIC)
                .addTypes(generateNested())
                .addMethod(new FromGrpcMethodGenerator(generationContext, generatedType(), protoType(), processedFields, annotateNotNull).generate())
                .addMethod(new ToGrpcMethodGenerator(generationContext, protoType(), processedFields, annotateNotNull).generate())
                .build();
    }

    private TypeSpec.Builder getRecordBuilder(List<FieldSpec> fields) {
        List<ParameterSpec> parameters = fields.stream()
                .map(Poem::fieldToParameter)
                .toList();
        return TypeSpec.recordBuilder(generatedType().simpleName())
                .addRecordComponents(parameters);
    }

    private TypeSpec.Builder getClassBuilder(List<FieldSpec> fields) {
        return TypeSpec.classBuilder(generatedType().simpleName())
                .addFields(fields)
                .addMethods(new ClassBoilerplateGenerator(generatedType(), fields).generateBoilerplate());
    }

    private List<TypeSpec> generateNested() {
        return msgDescriptor.getNested().stream()
                .map(generationContext.domains()::get)
                .filter(Objects::nonNull) // nested elements could be marked as not to generate
                .toList();
    }

    private ClassName protoType() {
        return generationContext.protoTypeTable().getType(msgDescriptor);
    }

    private ClassName generatedType() {
        return generationContext.domainTypeTable().getType(msgDescriptor);
    }
}
