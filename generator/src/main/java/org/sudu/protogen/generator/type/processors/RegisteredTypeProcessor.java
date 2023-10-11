package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.config.RegisteredTransformer;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RegisteredType;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public class RegisteredTypeProcessor extends TypeProcessor.Chain {

    public RegisteredTypeProcessor(GenerationContext context) {
        super(context);
    }

    @Override
    public @Nullable TypeModel processType(@NotNull EnumOrMessage type) {
        List<RegisteredTransformer> registered = getContext().configuration().registeredTransformers();
        for (var transformer : registered) {
            if (!type.getFullName().matches(transformer.protoType())) continue;
            return new RegisteredType(
                    transformer.javaClass(),
                    type.getProtobufTypeName(),
                    transformer
            );
        }
        return next(type);
    }
}
