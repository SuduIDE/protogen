package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.AbstractChain;

public interface TypeProcessor {

    @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor);

    abstract class Chain extends AbstractChain<Chain> implements TypeProcessor {

        private final GenerationContext context;

        public Chain(GenerationContext context) {
            this.context = context;
        }

        public GenerationContext getContext() {
            return context;
        }

        @Override
        public abstract @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor);

        protected final @Nullable TypeModel next(@NotNull EnumOrMessage descriptor) {
            if (getNext() != null) {
                return getNext().processType(descriptor);
            }
            return null;
        }

    }
}
