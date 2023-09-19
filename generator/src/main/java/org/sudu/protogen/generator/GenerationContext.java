package org.sudu.protogen.generator;

import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.field.processors.FieldTypeProcessor;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.processors.TypeProcessor;

public record GenerationContext(
        Configuration configuration,
        TypeProcessor typeProcessor,
        FieldTypeProcessor fieldTypeProcessor
) {

    public TypeModel processType(EnumOrMessage enumOrMessage) {
        return typeProcessor.processType(enumOrMessage, configuration);
    }

    public TypeModel processType(Field field) {
        return fieldTypeProcessor.processType(field, this);
    }
}
