package org.sudu.protogen.protoc.adaptor;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.protobuf.EnumOrMessage;
import org.sudu.protogen.protoc.Options;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Enum extends org.sudu.protogen.protobuf.Enum {

    Descriptors.EnumDescriptor descriptor;

    public Enum(Descriptors.EnumDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public List<? extends Value> getValues() {
        return descriptor.getValues().stream().map(EnumValue::new).toList();
    }

    @Override
    public @NotNull String getName() {
        return descriptor.getName();
    }

    @Override
    public @NotNull String getFullName() {
        return descriptor.getFullName();
    }

    @Override
    public @NotNull File getContainingFile() {
        return new File(descriptor.getFile());
    }

    @Override
    public @Nullable Message getContainingType() {
        return descriptor.getContainingType() == null ? null : new Message(descriptor.getContainingType());
    }

    @Override
    public List<EnumOrMessage> getNested() {
        return List.of();
    }

    @Override
    public Optional<Boolean> getDoGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.genEnum);
    }

    @Override
    public Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.enumName);
    }

    @Override
    protected Optional<String> getCustomClassNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.customEnum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enum anEnum = (Enum) o;
        return Objects.equals(descriptor, anEnum.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    public static class EnumValue extends Value {

        Descriptors.EnumValueDescriptor valueDescriptor;

        public EnumValue(Descriptors.EnumValueDescriptor valueDescriptor) {
            this.valueDescriptor = valueDescriptor;
        }

        @Override
        public String getName() {
            return valueDescriptor.getName();
        }

        @Override
        protected Optional<String> getOverriddenNameOption() {
            return Options.wrapExtension(valueDescriptor.getOptions(), protogen.Options.enumValName);
        }

        @Override
        protected Optional<Boolean> getUnusedOption() {
            return Options.wrapExtension(valueDescriptor.getOptions(), protogen.Options.unusedEnumVal);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EnumValue enumValue = (EnumValue) o;
            return Objects.equals(valueDescriptor, enumValue.valueDescriptor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(valueDescriptor);
        }
    }
}
