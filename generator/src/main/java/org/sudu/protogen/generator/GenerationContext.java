package org.sudu.protogen.generator;

import com.squareup.javapoet.TypeSpec;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.File;
import org.sudu.protogen.descriptors.Service;
import org.sudu.protogen.generator.field.processors.FieldTypeProcessor;
import org.sudu.protogen.generator.type.processors.TypeProcessor;

import java.util.List;
import java.util.Map;

public record GenerationContext(
        List<? extends File> filesToGenerate,
        Configuration configuration,
        TypeProcessor typeProcessor,
        FieldTypeProcessor fieldTypeProcessor,
        TypeTable protoTypeTable,
        TypeTable domainTypeTable,
        Map<EnumOrMessage, TypeSpec> domains,
        Map<Service, TypeSpec> clients
) {
}
