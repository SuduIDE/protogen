package org.sudu.protogen.generator;

import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.generator.field.processors.FieldTypeProcessor;
import org.sudu.protogen.generator.type.processors.TypeProcessor;

public record GenerationContext(
        Configuration configuration,
        TypeProcessor typeProcessor,
        FieldTypeProcessor fieldTypeProcessor
) {
}
