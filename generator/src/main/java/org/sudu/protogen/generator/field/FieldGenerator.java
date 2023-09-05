package org.sudu.protogen.generator.field;

import com.squareup.javapoet.FieldSpec;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

public class FieldGenerator {

    private final GenerationContext context;

    private final Field field;

    public FieldGenerator(GenerationContext context, Field field) {
        this.context = context;
        this.field = field;
    }

    @NotNull
    public FieldProcessingResult generate() {
        if (field.isIgnored()) {
            return FieldProcessingResult.empty(field);
        }

        TypeModel type = context.fieldTypeProcessor().processType(field, context);
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
