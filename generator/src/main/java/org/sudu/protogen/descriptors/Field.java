package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.protoc.Options;

import java.util.Objects;
import java.util.Optional;

public class Field {

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

    public @Nullable Message getMessageType() {
        return descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
                ? new Message(descriptor.getMessageType())
                : null;
    }

    public @Nullable Enum getEnumType() {
        return descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM
                ? new Enum(descriptor.getEnumType())
                : null;
    }

    public Message getContainingMessage() {
        return new Message(descriptor.getContainingType());
    }


    public final boolean isNullable() {
        return isOptional() || (isUnfolded() && getUnfoldedField().isNullable());
    }

    public final boolean isList() {
        return isRepeated();
    }

    public final boolean isMap() {
//        noinspection DataFlowIssue because getMessageType() != null iff type == MESSAGE
        return getType() == Type.MESSAGE
                && getMessageType().isMap();
    }

    public final boolean isUnfolded() {
        //noinspection DataFlowIssue because getMessageType() != null iff type == MESSAGE
        return getType() == Type.MESSAGE && getMessageType().isUnfolded();
    }

    public final boolean isIgnored() {
        return getUnusedFieldOption().orElse(false);
    }

    public final Field getUnfoldedField() {
        //noinspection DataFlowIssue because isUnfolded() true iff getMessageType() != null
        Validate.validState(isUnfolded());
        return getMessageType().getFields().get(0);
    }

    public final String getGeneratedName() {
        return getOverriddenNameOption()
                .orElseGet(() -> isUnfolded() ? getUnfoldedField().getGeneratedName() : getName());

    }

    public final RepeatedContainer getRepeatedContainer() {
        return getRepeatedContainerOption().orElse(RepeatedContainer.LIST);
    }

    /*
     * Options are not supposed to be used at high-level logic.
     * They return only the value of an option in .proto file.
     * Advanced logic taking into account other options and configuration values
     * is placed at top-level methods such as getGenerateOption for getGenerateOption.
     */

    public boolean isRepeated() {
        return descriptor.isRepeated();
    }

    public boolean isOptional() {
        return descriptor.hasOptionalKeyword();
    }

    public Optional<String> getOverriddenNameOption() {
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
}
