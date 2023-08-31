package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.protobuf.Field;

public class DomainFieldTypeProcessor extends FieldTypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
        if (field.getType() == Field.Type.MESSAGE)
            return context.typeProcessor().processType(field.getMessageType(), context);
        if (field.getType() == Field.Type.ENUM)
            return context.typeProcessor().processType(field.getEnumType(), context);
        return next(field, context);
    }
}
