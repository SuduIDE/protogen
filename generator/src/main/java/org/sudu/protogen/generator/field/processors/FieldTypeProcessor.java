package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.AbstractChain;

public interface FieldTypeProcessor {

    @NotNull
    TypeModel processType(@NotNull Field field);

    abstract class Chain extends AbstractChain<Chain> implements FieldTypeProcessor {

        private final @NotNull GenerationContext context;

        public Chain(@NotNull GenerationContext context) {
            this.context = context;
        }

        public @NotNull GenerationContext getContext() {
            return context;
        }

        @Override
        public @NotNull TypeModel processType(@NotNull Field field) {
            return next(field);
        }

        protected final @NotNull TypeModel next(@NotNull Field field) {
            if (getNext() != null) {
                return getNext().processType(field);
            }
            throw new IllegalArgumentException("Failed to generate Java type for the field " + field.getFullName());
        }
    }
}
