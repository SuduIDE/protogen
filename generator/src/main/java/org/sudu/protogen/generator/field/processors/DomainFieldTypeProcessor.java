package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

public class DomainFieldTypeProcessor extends FieldTypeProcessor.Chain {

    public DomainFieldTypeProcessor(@NotNull GenerationContext context) {
        super(context);
    }

    @Override
    public @NotNull TypeModel processType(@NotNull Field field) {
        EnumOrMessage descriptor = switch (field.getType()) {
            case MESSAGE -> field.getMessageType();
            case ENUM -> field.getEnumType();
            default -> null;
        };
        if (descriptor != null) {
            TypeModel type = getContext().typeManager().processType(descriptor);
            if (type != null) {
                return type;
            }
        }
        return next(field);
    }
}
