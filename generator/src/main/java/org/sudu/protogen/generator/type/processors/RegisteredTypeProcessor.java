package org.sudu.protogen.generator.type.processors;

import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.config.RegisteredTransformer;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RegisteredType;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Name;

import java.util.List;

class RegisteredTypeProcessor extends TypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull EnumOrMessage type, @NotNull GenerationContext context) {
        List<RegisteredTransformer> registered = context.configuration().registeredTransformers();
        for (var transformer : registered) {
            if (!type.getFullName().matches(transformer.protoType())) continue;
            return new RegisteredType(
                    ClassName.get(Name.getPackage(transformer.javaClass()), Name.getLastName(transformer.javaClass())),
                    type.getProtobufTypeName(),
                    transformer
            );
        }
        return next(type, context);
    }
}
