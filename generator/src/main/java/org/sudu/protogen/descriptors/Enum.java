package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.Options;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.enumeration.EnumGenerator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Enum extends EnumOrMessage {

    private final Descriptors.EnumDescriptor descriptor;

    public Enum(Descriptors.EnumDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public List<? extends Value> getValues() {
        return descriptor.getValues().stream().map(Value::new).toList();
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
    public TypeSpec generate(GenerationContext context) {
        return new EnumGenerator(context, this).generate();
    }

    @Override
    protected Optional<Boolean> getDoGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.genEnum);
    }

    @Override
    protected Optional<String> getOverriddenNameOption() {
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

    public static class Value {

        private final Descriptors.EnumValueDescriptor valueDescriptor;

        public Value(Descriptors.EnumValueDescriptor valueDescriptor) {
            this.valueDescriptor = valueDescriptor;
        }

        public final String generatedName() {
            return getOverriddenNameOption().orElse(getName());
        }

        public final boolean isUnused() {
            return getUnusedOption().orElse(false);
        }

        public String getName() {
            return valueDescriptor.getName();
        }

        protected Optional<String> getOverriddenNameOption() {
            return Options.wrapExtension(valueDescriptor.getOptions(), protogen.Options.enumValName);
        }

        protected Optional<Boolean> getUnusedOption() {
            return Options.wrapExtension(valueDescriptor.getOptions(), protogen.Options.unusedEnumVal);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value value = (Value) o;
            return Objects.equals(valueDescriptor, value.valueDescriptor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(valueDescriptor);
        }
    }
}
