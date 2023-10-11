package org.sudu.protogen.generator.field.processors;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Field;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.PrimitiveTypeModel;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;

public class UnfoldedFieldTypeProcessor extends FieldTypeProcessor.Chain {

    public UnfoldedFieldTypeProcessor(@NotNull GenerationContext context) {
        super(context);
    }

    @Override
    public @NotNull TypeModel processType(@NotNull Field field) {
        if (field.isUnfolded()) {
            Field unfoldedField = field.getUnfoldedField();
            TypeModel unfoldedType = getContext().typeManager().processType(unfoldedField);
            if (unfoldedType instanceof PrimitiveTypeModel primType && field.isNullable()) {
                unfoldedType = new TypeModel(primType.getTypeName().box());
            }
            TypeModel processedModel = new UnfoldedType(
                    unfoldedType,
                    unfoldedField.getContainingMessage()
            );
            if (field.isList()) {
                return new RepeatedType(
                        processedModel,
                        field.getRepeatedContainer()
                );
            }
            return processedModel;
        }
        return next(field);
    }
}
