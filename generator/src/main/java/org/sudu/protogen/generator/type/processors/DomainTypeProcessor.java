package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.DomainType;
import org.sudu.protogen.generator.type.TypeModel;

class DomainTypeProcessor extends TypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull EnumOrMessage type, @NotNull GenerationContext context) {
        if (type.doGenerate() || type.customClass() != null) {
            return new DomainType(type.getGeneratedTypeName(context.configuration().namingManager()));
        } else {
            throw new IllegalArgumentException((
                    "It's not possible to process type of %s because it doesn't have a domain object. " +
                            "Specify an existing domain object using custom_class option or generate it."
            ).formatted(type.getFullName()));
        }
    }
}
