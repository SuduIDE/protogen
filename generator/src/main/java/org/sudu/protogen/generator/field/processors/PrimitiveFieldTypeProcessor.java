package org.sudu.protogen.generator.field.processors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.PrimitiveTypeModel;
import org.sudu.protogen.generator.type.TypeModel;

class PrimitiveFieldTypeProcessor extends FieldTypeProcessor.Chain {

    public PrimitiveFieldTypeProcessor(@NotNull GenerationContext context) {
        super(context);
    }

    @Override
    public @NotNull TypeModel processType(@NotNull Field field) {
        return switch (field.getType()) {
            case INT -> boxIfNullable(field, TypeName.INT);
            case LONG -> boxIfNullable(field, TypeName.LONG);
            case FLOAT -> boxIfNullable(field, TypeName.FLOAT);
            case DOUBLE -> boxIfNullable(field, TypeName.DOUBLE);
            case BOOLEAN -> boxIfNullable(field, TypeName.BOOLEAN);
            case STRING -> new TypeModel(ClassName.get(String.class));
            case BYTE_STRING -> new TypeModel(ClassName.get("com.google.protobuf", "ByteString"));
            default -> next(field);
        };
    }

    private TypeModel boxIfNullable(@NotNull Field field, TypeName primitive) {
        return field.isNullable()
                ? new TypeModel(primitive.box())
                : new PrimitiveTypeModel(primitive);
    }
}
