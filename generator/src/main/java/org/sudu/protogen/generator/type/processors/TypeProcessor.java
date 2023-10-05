package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public interface TypeProcessor {

    @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor);

    abstract class Chain implements TypeProcessor {

        private final GenerationContext context;

        private @Nullable Chain next;

        public Chain(GenerationContext context) {
            this.context = context;
        }

        public GenerationContext getContext() {
            return context;
        }

        public static TypeProcessor getProcessingChain(GenerationContext context) {

            var chain = List.of( // Ordering is important!
                    new RegisteredTypeProcessor(context),
                    new DomainTypeProcessor(context),
                    new EmptyMessageProcessor(context)
            );
            for (int i = 0; i < chain.size() - 1; ++i) {
                var current = chain.get(i);
                var next = chain.get(i + 1);
                current.setNext(next);
            }
            return chain.get(0);
        }

        @Override
        public abstract @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor);

        protected final @Nullable TypeModel next(@NotNull EnumOrMessage descriptor) {
            if (next != null) {
                return next.processType(descriptor);
            }
            return null;
        }

        private void setNext(@NotNull Chain typeProcessor) {
            this.next = typeProcessor;
        }

    }
}
