package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.ProtogenException;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.protobuf.Field;

import java.util.List;

public interface FieldTypeProcessor {

    @NotNull
    TypeModel processType(@NotNull Field field, @NotNull GenerationContext context);

    abstract class Chain implements FieldTypeProcessor {

        private @Nullable FieldTypeProcessor.Chain next;

        public static FieldTypeProcessor getProcessingChain() {

            var chain = List.of( // Ordering is important!
                    new UnfoldedFieldTypeProcessor(),
                    new MapFieldTypeProcessor(),
                    new ListFieldTypeProcessor(),
                    new PrimitiveFieldTypeProcessor(),
                    new DomainFieldTypeProcessor()
            );
            for (int i = 0; i < chain.size() - 1; ++i) {
                var current = chain.get(i);
                var next = chain.get(i + 1);
                current.setNext(next);
            }
            return chain.get(0);
        }

        @Override
        public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
            return next(field, context);
        }

        private void setNext(@NotNull FieldTypeProcessor.Chain typeProcessor) {
            this.next = typeProcessor;
        }

        protected final @NotNull TypeModel next(@NotNull Field field, @NotNull GenerationContext context) {
            if (next != null) {
                return next.processType(field, context);
            }
            throw new ProtogenException("Failed to generate Java type for the field " + field.getName());
        }
    }
}
