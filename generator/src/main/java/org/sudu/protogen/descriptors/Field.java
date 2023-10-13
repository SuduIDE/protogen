package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.Options;

import java.util.Objects;
import java.util.Optional;

public class Field implements Descriptor {

    private final Descriptors.FieldDescriptor descriptor;

    public Field(Descriptors.FieldDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public String getName() {
        return descriptor.getName();
    }

    public String getFullName() {
        return descriptor.getFullName();
    }

    public Type getType() {
        return mapType(descriptor.getJavaType());
    }

    public @NotNull Message getMessageType() {
        Validate.validState(getType() == Type.MESSAGE);
        return new Message(descriptor.getMessageType());
    }

    public @NotNull Enum getEnumType() {
        Validate.validState(getType() == Type.ENUM);
        return new Enum(descriptor.getEnumType());
    }

    public Message getContainingMessage() {
        return new Message(descriptor.getContainingType());
    }

    public final boolean isNullable() {
        return isOptional() || (isUnfolded() && getUnfoldedField().isNullable()) || descriptor.getContainingOneof() != null;
    }

    public final boolean isList() {
        return isRepeated();
    }

    public final boolean isMap() {
        return getType() == Type.MESSAGE
                && getMessageType().isMap();
    }

    public final boolean isUnfolded() {
        return getType() == Type.MESSAGE && getMessageType().isUnfolded();
    }

    public final boolean isUnused() {
        return getUnusedFieldOption().orElse(false);
    }

    public final Field getUnfoldedField() {
        Validate.validState(isUnfolded());
        return getMessageType().getFields().get(0);
    }

    public final String getGeneratedName() {
        return getOverriddenNameOption().orElseGet(this::getName);

    }

    public final RepeatedContainer getRepeatedContainer() {
        return getRepeatedContainerOption().orElse(RepeatedContainer.LIST);
    }

    // -----------

    protected boolean isRepeated() {
        return descriptor.isRepeated();
    }

    protected boolean isOptional() {
        return descriptor.hasOptionalKeyword();
    }

    protected Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.fieldName);
    }

    protected Optional<RepeatedContainer> getRepeatedContainerOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.repeatedContainer)
                .map(RepeatedContainer::fromGrpc);
    }

    protected Optional<Boolean> getUnusedFieldOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.unusedField);
    }

    private Type mapType(Descriptors.FieldDescriptor.JavaType javaType) {
        return switch (javaType) {
            case INT -> org.sudu.protogen.descriptors.Field.Type.INT;
            case DOUBLE -> org.sudu.protogen.descriptors.Field.Type.DOUBLE;
            case FLOAT -> org.sudu.protogen.descriptors.Field.Type.FLOAT;
            case LONG -> org.sudu.protogen.descriptors.Field.Type.LONG;
            case BOOLEAN -> org.sudu.protogen.descriptors.Field.Type.BOOLEAN;
            case MESSAGE -> org.sudu.protogen.descriptors.Field.Type.MESSAGE;
            case ENUM -> org.sudu.protogen.descriptors.Field.Type.ENUM;
            case STRING -> org.sudu.protogen.descriptors.Field.Type.STRING;
            case BYTE_STRING -> org.sudu.protogen.descriptors.Field.Type.BYTE_STRING;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(descriptor, field.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    public enum Type {
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        STRING,
        BYTE_STRING,
        ENUM,
        MESSAGE
    }
}
