package org.sudu.protogen.generator.field;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.type.TypeModel;

public record FieldProcessingResult(
        @NotNull Field original,
        @NotNull FieldSpec field,
        @NotNull TypeModel type,
        boolean isNullable
) {

    public static FieldProcessingResult empty(Field original) {
        return new FieldProcessingResult(
                original,
                FieldSpec.builder(TypeName.VOID, "empty").build(),
                new TypeModel(TypeName.VOID),
                false
        );
    }

    public boolean isEmpty() {
        return type.getTypeName().equals(TypeName.VOID);
    }

    public boolean isNonEmpty() {
        return !isEmpty();
    }
}
