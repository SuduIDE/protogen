package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;

class ListFieldTypeProcessor extends FieldTypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
        TypeModel type = next(field, context);
        return field.isList()
                ? new RepeatedType(type, field.getRepeatedContainer())
                : type;
    }
}
