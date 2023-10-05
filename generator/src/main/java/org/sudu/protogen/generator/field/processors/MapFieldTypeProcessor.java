package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.MapType;
import org.sudu.protogen.generator.type.TypeModel;

class MapFieldTypeProcessor extends FieldTypeProcessor.Chain {

    public MapFieldTypeProcessor(@NotNull GenerationContext context) {
        super(context);
    }

    @Override
    public @NotNull TypeModel processType(@NotNull Field field) {
        if (!field.isMap()) {
            return next(field);
        }
        Message entryDescriptor = field.getMessageType();
        Field key = entryDescriptor.getFields().stream()
                .filter(f -> f.getName().equals("key")).findFirst().orElseThrow();
        Field value = entryDescriptor.getFields().stream()
                .filter(f -> f.getName().equals("value")).findFirst().orElseThrow();
        return new MapType(next(key), next(value));
    }
}
