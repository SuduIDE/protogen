package org.sudu.protogen.generator.field;

import com.squareup.javapoet.FieldSpec;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.DescriptorGenerator;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

public class FieldGenerator implements DescriptorGenerator<Field, FieldProcessingResult> {

    private final GenerationContext context;

    public FieldGenerator(GenerationContext context) {
        this.context = context;
    }

    @NotNull
    public FieldProcessingResult generate(Field field) {
        if (field.isUnused()) {
            return FieldProcessingResult.empty(field);
        }

        TypeModel type = context.typeManager().processType(field);
        String identifier = field.getGeneratedName();
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(type.getTypeName(), identifier);

        boolean isNullable = field.isNullable();

        if (!type.isPrimitive()) {
            if (field.getContainingMessage().getContainingFile().doUseNullabilityAnnotation(isNullable)) {
                Poem.attachNullabilityAnnotations(fieldSpecBuilder, context, isNullable);
            }
        }

        return new FieldProcessingResult(field, fieldSpecBuilder.build(), type, isNullable);
    }
}
