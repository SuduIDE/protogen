package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.Options;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Enum extends EnumOrMessage implements Descriptor {

    private final Descriptors.EnumDescriptor enumDescriptor;

    public Enum(Descriptors.EnumDescriptor enumDescriptor) {
        this.enumDescriptor = enumDescriptor;
    }

    public List<? extends Value> getValues() {
        return enumDescriptor.getValues().stream().map(Value::new).toList();
    }

    @Override
    public @NotNull String getName() {
        return enumDescriptor.getName();
    }

    @Override
    public @NotNull String getFullName() {
        return enumDescriptor.getFullName();
    }

    @Override
    public @NotNull List<EnumOrMessage> getNested() {
        return List.of();
    }

    @Override
    public @NotNull File getContainingFile() {
        return new File(enumDescriptor.getFile());
    }

    @Override
    public @Nullable Message getContainingType() {
        return Optional.ofNullable(enumDescriptor.getContainingType())
                .map(Message::new)
                .orElse(null);
    }

    @Override
    public @Nullable String getCustomClass() {
        return Options.wrapExtension(enumDescriptor.getOptions(), protogen.Options.customEnum).orElse(null);
    }

    // -----------------

    @Override
    protected Optional<Boolean> getDoGenerateOption() {
        return Options.wrapExtension(enumDescriptor.getOptions(), protogen.Options.genEnum);
    }

    @Override
    protected Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(enumDescriptor.getOptions(), protogen.Options.enumName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enum anEnum = (Enum) o;
        return Objects.equals(enumDescriptor, anEnum.enumDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumDescriptor);
    }

    public static class Value {

        private final Descriptors.EnumValueDescriptor valueDescriptor;

        public Value(Descriptors.EnumValueDescriptor valueDescriptor) {
            this.valueDescriptor = valueDescriptor;
        }

        public String generatedName() {
            return getOverriddenNameOption().orElse(getName());
        }

        public boolean isUnused() {
            return getUnusedOption().orElse(false);
        }

        public String getName() {
            return valueDescriptor.getName();
        }

        // -----------------

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
