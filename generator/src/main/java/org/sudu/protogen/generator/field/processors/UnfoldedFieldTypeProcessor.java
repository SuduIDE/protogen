package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.PrimitiveTypeModel;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;
import org.sudu.protogen.utils.Poem;

class UnfoldedFieldTypeProcessor extends FieldTypeProcessor.Chain {

    @Override
    public @NotNull TypeModel processType(@NotNull Field field, @NotNull GenerationContext context) {
        if (field.isUnfolded()) {
            Field unfoldedField = field.getUnfoldedField();
            TypeModel unfoldedType = context.fieldTypeProcessor().processType(unfoldedField, context);
            if (unfoldedType instanceof PrimitiveTypeModel primType && field.isNullable()) {
                unfoldedType = new TypeModel(primType.getTypeName().box());
            }
            TypeModel processedModel = new UnfoldedType(
                    unfoldedType,
                    unfoldedField.getName(),
                    Poem.className(unfoldedField.getContainingMessage().getFullName())
            );
            if (field.isList()) {
                return new RepeatedType(
                        processedModel,
                        field.getRepeatedContainer()
                );
            }
            return processedModel;
        }
        return next(field, context);
    }
}
