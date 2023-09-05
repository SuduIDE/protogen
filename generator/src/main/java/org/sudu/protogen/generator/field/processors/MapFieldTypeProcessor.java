package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.MapType;
import org.sudu.protogen.generator.type.TypeModel;

class MapFieldTypeProcessor extends FieldTypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
        if (!field.isMap()) {
            return next(field, context);
        }
        Message entryDescriptor = field.getMessageType();
        Field key = entryDescriptor.getFields().stream()
                .filter(f -> f.getName().equals("key")).findFirst().orElseThrow();
        Field value = entryDescriptor.getFields().stream()
                .filter(f -> f.getName().equals("value")).findFirst().orElseThrow();
        return new MapType(
                context.fieldTypeProcessor().processType(key, context),
                context.fieldTypeProcessor().processType(value, context)
        );
    }
}
