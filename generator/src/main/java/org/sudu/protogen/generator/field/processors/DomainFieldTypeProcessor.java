package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

public class DomainFieldTypeProcessor extends FieldTypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
        EnumOrMessage descriptor = switch (field.getType()) {
            case MESSAGE -> field.getMessageType();
            case ENUM -> field.getEnumType();
            default -> null;
        };
        if (descriptor != null) {
            TypeModel type = context.typeProcessor().processType(descriptor, context.configuration());
            if (type != null) {
                return type;
            }
        }
        return next(field, context);
    }
}
