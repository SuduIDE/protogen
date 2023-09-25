package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.type.DomainType;
import org.sudu.protogen.generator.type.TypeModel;

class DomainTypeProcessor extends TypeProcessor.Chain {

    @Override
    public @Nullable TypeModel processType(@NotNull EnumOrMessage type, @NotNull Configuration configuration) {
        if (type.doGenerate() || type.getCustomClass() != null) {
            return new DomainType(type.getDomainTypeName(configuration.namingManager()));
        }
//            throw new IllegalArgumentException((
//                    "It's not possible to process type of %s because it doesn't have a domain object. " +
//                            "Specify an existing domain object using custom_class option or generate it."
//            ).formatted(type.getFullName())); todo move 2 type processor
        return next(type, configuration);
    }
}
