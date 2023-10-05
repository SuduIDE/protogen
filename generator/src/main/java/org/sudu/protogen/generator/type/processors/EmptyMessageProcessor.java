package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.VoidType;

/**
 * Treats all empty messages as void
 */
public class EmptyMessageProcessor extends TypeProcessor.Chain {


    public EmptyMessageProcessor(GenerationContext context) {
        super(context);
    }

    @Override
    public @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor) {
        if (descriptor instanceof Message msg) {
            if (msg.getFields().isEmpty()) return new VoidType();
        }
        return next(descriptor);
    }
}
