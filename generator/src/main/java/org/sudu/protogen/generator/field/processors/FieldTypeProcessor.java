package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;

import java.util.List;

public interface FieldTypeProcessor {

    @NotNull
    TypeModel processType(@NotNull Field field);

    abstract class Chain implements FieldTypeProcessor {

        private final @NotNull GenerationContext context;

        private @Nullable FieldTypeProcessor.Chain next;

        public Chain(@NotNull GenerationContext context) {
            this.context = context;
        }

        public @NotNull GenerationContext getContext() {
            return context;
        }

        public static FieldTypeProcessor getProcessingChain(GenerationContext context) {

            var chain = List.of( // Ordering is important!
                    new UnfoldedFieldTypeProcessor(context),
                    new MapFieldTypeProcessor(context),
                    new ListFieldTypeProcessor(context),
                    new PrimitiveFieldTypeProcessor(context),
                    new DomainFieldTypeProcessor(context)
            );
            for (int i = 0; i < chain.size() - 1; ++i) {
                var current = chain.get(i);
                var next = chain.get(i + 1);
                current.setNext(next);
            }
            return chain.get(0);
        }

        @Override
        public @NotNull TypeModel processType(@NotNull Field field) {
            return next(field);
        }

        private void setNext(@NotNull FieldTypeProcessor.Chain typeProcessor) {
            this.next = typeProcessor;
        }

        protected final @NotNull TypeModel next(@NotNull Field field) {
            if (next != null) {
                return next.processType(field);
            }
            throw new IllegalArgumentException("Failed to generate Java type for the field " + field.getFullName());
        }
    }
}
