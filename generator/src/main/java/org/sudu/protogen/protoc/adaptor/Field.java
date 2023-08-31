package org.sudu.protogen.protoc.adaptor;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.protoc.Options;

import java.util.Objects;
import java.util.Optional;

public class Field extends org.sudu.protogen.protobuf.Field {

    Descriptors.FieldDescriptor descriptor;

    public Field(Descriptors.FieldDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return descriptor.getName();
    }

    @Override
    public String getFullName() {
        return descriptor.getFullName();
    }

    @Override
    public Type getType() {
        return mapType(descriptor.getJavaType());
    }

    @Override
    public @Nullable Message getMessageType() {
        return descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
                ? new Message(descriptor.getMessageType())
                : null;
    }

    @Override
    public @Nullable Enum getEnumType() {
        return descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM
                ? new Enum(descriptor.getEnumType())
                : null;
    }

    @Override
    public Message getContainingMessage() {
        return new Message(descriptor.getContainingType());
    }

    @Override
    public boolean isRepeated() {
        return descriptor.isRepeated();
    }

    @Override
    public boolean isOptional() {
        return descriptor.hasOptionalKeyword();
    }

    @Override
    public Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.fieldName);
    }

    @Override
    protected Optional<RepeatedContainer> getRepeatedContainerOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.repeatedContainer)
                .map(RepeatedContainer::fromGrpc);
    }

    @Override
    protected Optional<Boolean> getUnusedFieldOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.unusedField);
    }

    private Field.Type mapType(Descriptors.FieldDescriptor.JavaType javaType) {
        return switch (javaType) {
            case INT -> org.sudu.protogen.protobuf.Field.Type.INT;
            case DOUBLE -> org.sudu.protogen.protobuf.Field.Type.DOUBLE;
            case FLOAT -> org.sudu.protogen.protobuf.Field.Type.FLOAT;
            case LONG -> org.sudu.protogen.protobuf.Field.Type.LONG;
            case BOOLEAN -> org.sudu.protogen.protobuf.Field.Type.BOOLEAN;
            case MESSAGE -> org.sudu.protogen.protobuf.Field.Type.MESSAGE;
            case ENUM -> org.sudu.protogen.protobuf.Field.Type.ENUM;
            case STRING -> org.sudu.protogen.protobuf.Field.Type.STRING;
            case BYTE_STRING -> org.sudu.protogen.protobuf.Field.Type.BYTE_STRING;
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
}
