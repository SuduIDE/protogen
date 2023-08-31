package org.sudu.protogen.protobuf;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EnumMock extends Enum {

    String name;
    String fullName;
    File containingFile;
    Message containingType;
    List<? extends EnumOrMessage> nested;
    List<? extends Value> values;
    Optional<String> customClassNameOption;
    Optional<Boolean> doGenerateOption;
    Optional<String> overriddenNameOption;


    public EnumMock(
            String name,
            String fullName,
            File containingFile,
            Message containingType,
            List<? extends EnumOrMessage> nested,
            List<? extends Value> values,
            Optional<String> customClassNameOption,
            Optional<Boolean> doGenerateOption,
            Optional<String> overriddenNameOption
    ) {
        this.name = name;
        this.fullName = fullName;
        this.containingFile = containingFile;
        this.containingType = containingType;
        this.nested = nested;
        this.values = values;
        this.customClassNameOption = customClassNameOption;
        this.doGenerateOption = doGenerateOption;
        this.overriddenNameOption = overriddenNameOption;
    }

    private static List<? extends EnumOrMessage> $default$nested() {
        return List.of();
    }

    private static List<? extends Value> $default$values() {
        return List.of();
    }

    private static Optional<String> $default$customClassNameOption() {
        return Optional.empty();
    }

    private static Optional<Boolean> $default$doGenerateOption() {
        return Optional.empty();
    }

    private static Optional<String> $default$overriddenNameOption() {
        return Optional.empty();
    }

    public static EnumMockBuilder builder() {
        return new EnumMockBuilder();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public File getContainingFile() {
        return this.containingFile;
    }

    public void setContainingFile(File containingFile) {
        this.containingFile = containingFile;
    }

    public Message getContainingType() {
        return this.containingType;
    }

    public void setContainingType(Message containingType) {
        this.containingType = containingType;
    }

    public List<? extends EnumOrMessage> getNested() {
        return this.nested;
    }

    public void setNested(List<? extends EnumOrMessage> nested) {
        this.nested = nested;
    }

    public List<? extends Value> getValues() {
        return this.values;
    }

    public void setValues(List<? extends Value> values) {
        this.values = values;
    }

    public Optional<String> getCustomClassNameOption() {
        return this.customClassNameOption;
    }

    public void setCustomClassNameOption(Optional<String> customClassNameOption) {
        this.customClassNameOption = customClassNameOption;
    }

    public Optional<Boolean> getDoGenerateOption() {
        return this.doGenerateOption;
    }

    public void setDoGenerateOption(Optional<Boolean> doGenerateOption) {
        this.doGenerateOption = doGenerateOption;
    }

    public Optional<String> getOverriddenNameOption() {
        return this.overriddenNameOption;
    }

    public void setOverriddenNameOption(Optional<String> overriddenNameOption) {
        this.overriddenNameOption = overriddenNameOption;
    }

    @Override
    public String toString() {
        return "EnumMock{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", nested=" + nested +
                ", values=" + values +
                ", customClassNameOption=" + customClassNameOption +
                ", doGenerateOption=" + doGenerateOption +
                ", overriddenNameOption=" + overriddenNameOption +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumMock enumMock = (EnumMock) o;
        return Objects.equals(name, enumMock.name) && Objects.equals(fullName, enumMock.fullName) && Objects.equals(nested, enumMock.nested) && Objects.equals(values, enumMock.values) && Objects.equals(customClassNameOption, enumMock.customClassNameOption) && Objects.equals(doGenerateOption, enumMock.doGenerateOption) && Objects.equals(overriddenNameOption, enumMock.overriddenNameOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fullName, nested, values, customClassNameOption, doGenerateOption, overriddenNameOption);
    }

    public static class EnumMockBuilder {
        private String name;
        private String fullName;
        private File containingFile;
        private Message containingType;
        private List<? extends EnumOrMessage> nested$value;
        private boolean nested$set;
        private List<? extends Value> values$value;
        private boolean values$set;
        private Optional<String> customClassNameOption$value;
        private boolean customClassNameOption$set;
        private Optional<Boolean> doGenerateOption$value;
        private boolean doGenerateOption$set;
        private Optional<String> overriddenNameOption$value;
        private boolean overriddenNameOption$set;

        EnumMockBuilder() {
        }

        public EnumMockBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EnumMockBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public EnumMockBuilder containingFile(File containingFile) {
            this.containingFile = containingFile;
            return this;
        }

        public EnumMockBuilder containingType(Message containingType) {
            this.containingType = containingType;
            return this;
        }

        public EnumMockBuilder nested(List<? extends EnumOrMessage> nested) {
            this.nested$value = nested;
            this.nested$set = true;
            return this;
        }

        public EnumMockBuilder values(List<? extends Value> values) {
            this.values$value = values;
            this.values$set = true;
            return this;
        }

        public EnumMockBuilder customClassNameOption(Optional<String> customClassNameOption) {
            this.customClassNameOption$value = customClassNameOption;
            this.customClassNameOption$set = true;
            return this;
        }

        public EnumMockBuilder doGenerateOption(Optional<Boolean> doGenerateOption) {
            this.doGenerateOption$value = doGenerateOption;
            this.doGenerateOption$set = true;
            return this;
        }

        public EnumMockBuilder overriddenNameOption(Optional<String> overriddenNameOption) {
            this.overriddenNameOption$value = overriddenNameOption;
            this.overriddenNameOption$set = true;
            return this;
        }

        public EnumMock build() {
            List<? extends EnumOrMessage> nested$value = this.nested$value;
            if (!this.nested$set) {
                nested$value = EnumMock.$default$nested();
            }
            List<? extends Value> values$value = this.values$value;
            if (!this.values$set) {
                values$value = EnumMock.$default$values();
            }
            Optional<String> customClassNameOption$value = this.customClassNameOption$value;
            if (!this.customClassNameOption$set) {
                customClassNameOption$value = EnumMock.$default$customClassNameOption();
            }
            Optional<Boolean> doGenerateOption$value = this.doGenerateOption$value;
            if (!this.doGenerateOption$set) {
                doGenerateOption$value = EnumMock.$default$doGenerateOption();
            }
            Optional<String> overriddenNameOption$value = this.overriddenNameOption$value;
            if (!this.overriddenNameOption$set) {
                overriddenNameOption$value = EnumMock.$default$overriddenNameOption();
            }
            return new EnumMock(this.name, this.fullName, this.containingFile, this.containingType, nested$value, values$value, customClassNameOption$value, doGenerateOption$value, overriddenNameOption$value);
        }
    }
}
