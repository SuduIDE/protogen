package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public interface TypeProcessor {

    @NotNull
    TypeModel processType(@NotNull EnumOrMessage descriptor, @NotNull GenerationContext context);

    abstract class Chain implements TypeProcessor {

        private @Nullable Chain next;

        public static TypeProcessor getProcessingChain() {

            var chain = List.of( // Ordering is important!
                    new RegisteredTypeProcessor(),
                    new DomainTypeProcessor()
            );
            for (int i = 0; i < chain.size() - 1; ++i) {
                var current = chain.get(i);
                var next = chain.get(i + 1);
                current.setNext(next);
            }
            return chain.get(0);
        }

        @Override
        public @NotNull TypeModel processType(@NotNull EnumOrMessage descriptor, @NotNull GenerationContext context) {
            return next(descriptor, context);
        }

        private void setNext(@NotNull Chain typeProcessor) {
            this.next = typeProcessor;
        }

        protected final @NotNull TypeModel next(@NotNull EnumOrMessage descriptor, @NotNull GenerationContext context) {
            if (next != null) {
                return next.processType(descriptor, context);
            }
            throw new IllegalArgumentException("Failed to generate a Java type for " + descriptor.getFullName());
        }
    }
}
