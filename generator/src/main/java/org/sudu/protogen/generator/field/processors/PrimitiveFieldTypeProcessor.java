package org.sudu.protogen.generator.field.processors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.PrimitiveTypeModel;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.protobuf.Field;

class PrimitiveFieldTypeProcessor extends FieldTypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
        return switch (field.getType()) {
            case INT -> boxIfNullable(field, TypeName.INT);
            case LONG -> boxIfNullable(field, TypeName.LONG);
            case FLOAT -> boxIfNullable(field, TypeName.FLOAT);
            case DOUBLE -> boxIfNullable(field, TypeName.DOUBLE);
            case BOOLEAN -> boxIfNullable(field, TypeName.BOOLEAN);
            case STRING -> new TypeModel(ClassName.get(String.class));
            case BYTE_STRING -> new TypeModel(ClassName.get("com.google.protobuf", "ByteString"));
            default -> next(field, context);
        };
    }

    private TypeModel boxIfNullable(@NotNull Field field, TypeName primitive) {
        return field.isNullable() || field.isList()
                ? new TypeModel(primitive.box())
                : new PrimitiveTypeModel(primitive);
    }
}
