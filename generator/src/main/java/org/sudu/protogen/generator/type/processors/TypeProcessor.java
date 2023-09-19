package org.sudu.protogen.generator.type.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public interface TypeProcessor {

    @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor, @NotNull Configuration configuration);

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
        public @Nullable TypeModel processType(@NotNull EnumOrMessage descriptor, @NotNull Configuration configuration) {
            return next(descriptor, configuration);
        }

        private void setNext(@NotNull Chain typeProcessor) {
            this.next = typeProcessor;
        }

        protected final @Nullable TypeModel next(@NotNull EnumOrMessage descriptor, @NotNull Configuration configuration) {
            if (next != null) {
                return next.processType(descriptor, configuration);
            }
            return null;
        }
    }
}
