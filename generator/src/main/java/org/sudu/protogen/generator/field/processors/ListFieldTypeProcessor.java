package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;

public class ListFieldTypeProcessor extends FieldTypeProcessor.Chain {

    public ListFieldTypeProcessor(@NotNull GenerationContext context) {
        super(context);
    }

    @Override
    public @NotNull TypeModel processType(@NotNull Field field) {
        TypeModel type = next(field);
        return field.isList()
                ? new RepeatedType(type, field.getRepeatedContainer())
                : type;
    }
}
