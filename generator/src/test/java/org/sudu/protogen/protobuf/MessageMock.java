package org.sudu.protogen.protobuf;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MessageMock extends Message {

    String name;
    String fullName;
    File containingFile;
    Message containingType;
    List<? extends EnumOrMessage> nested;
    List<? extends Field> fields;
    boolean map;
    Optional<Boolean> decomposeOption;
    Optional<String> customClassNameOption;
    Optional<String> overriddenNameOption;
    Optional<Boolean> DoGenerateOption;
    Optional<Boolean> unfoldOption;


    public MessageMock(
            String name,
            String fullName,
            File containingFile,
            Message containingType,
            List<? extends EnumOrMessage> nested,
            List<? extends Field> fields,
            boolean map,
            Optional<Boolean> decomposeOption,
            Optional<String> customClassNameOption,
            Optional<String> overriddenNameOption,
            Optional<Boolean> DoGenerateOption,
            Optional<Boolean> unfoldOption
    ) {
        this.name = name;
        this.fullName = fullName;
        this.containingFile = containingFile;
        this.containingType = containingType;
        this.nested = nested;
        this.fields = fields;
        this.map = map;
        this.decomposeOption = decomposeOption;
        this.customClassNameOption = customClassNameOption;
        this.overriddenNameOption = overriddenNameOption;
        this.DoGenerateOption = DoGenerateOption;
        this.unfoldOption = unfoldOption;
    }

    private static List<? extends EnumOrMessage> $default$nested() {
        return List.of();
    }

    private static List<? extends Field> $default$fields() {
        return List.of();
    }

    private static boolean $default$map() {
        return false;
    }

    private static Optional<Boolean> $default$decomposeOption() {
        return Optional.empty();
    }

    private static Optional<String> $default$customClassNameOption() {
        return Optional.empty();
    }

    private static Optional<String> $default$overriddenNameOption() {
        return Optional.empty();
    }

    private static Optional<Boolean> $default$DoGenerateOption() {
        return Optional.empty();
    }

    private static Optional<Boolean> $default$unfoldOption() {
        return Optional.empty();
    }

    public static MessageMockBuilder builder() {
        return new MessageMockBuilder();
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

    public List<? extends Field> getFields() {
        return this.fields;
    }

    public void setFields(List<? extends Field> fields) {
        this.fields = fields;
    }

    public boolean isMap() {
        return this.map;
    }

    public void setMap(boolean map) {
        this.map = map;
    }

    public Optional<Boolean> getDecomposeOption() {
        return this.decomposeOption;
    }

    public void setDecomposeOption(Optional<Boolean> decomposeOption) {
        this.decomposeOption = decomposeOption;
    }

    public Optional<String> getCustomClassNameOption() {
        return this.customClassNameOption;
    }

    public void setCustomClassNameOption(Optional<String> customClassNameOption) {
        this.customClassNameOption = customClassNameOption;
    }

    public Optional<String> getOverriddenNameOption() {
        return this.overriddenNameOption;
    }

    public void setOverriddenNameOption(Optional<String> overriddenNameOption) {
        this.overriddenNameOption = overriddenNameOption;
    }

    public Optional<Boolean> getDoGenerateOption() {
        return this.DoGenerateOption;
    }

    public void setDoGenerateOption(Optional<Boolean> DoGenerateOption) {
        this.DoGenerateOption = DoGenerateOption;
    }

    public Optional<Boolean> getUnfoldOption() {
        return this.unfoldOption;
    }

    public void setUnfoldOption(Optional<Boolean> unfoldOption) {
        this.unfoldOption = unfoldOption;
    }

    @Override
    public Optional<String> getComparatorReference() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "MessageMock{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", nested=" + nested +
                ", fields=" + fields +
                ", map=" + map +
                ", decomposeOption=" + decomposeOption +
                ", customClassNameOption=" + customClassNameOption +
                ", overriddenNameOption=" + overriddenNameOption +
                ", DoGenerateOption=" + DoGenerateOption +
                ", unfoldOption=" + unfoldOption +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageMock that = (MessageMock) o;
        return map == that.map && Objects.equals(name, that.name) && Objects.equals(fullName, that.fullName) && Objects.equals(nested, that.nested) && Objects.equals(fields, that.fields) && Objects.equals(decomposeOption, that.decomposeOption) && Objects.equals(customClassNameOption, that.customClassNameOption) && Objects.equals(overriddenNameOption, that.overriddenNameOption) && Objects.equals(DoGenerateOption, that.DoGenerateOption) && Objects.equals(unfoldOption, that.unfoldOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fullName, nested, fields, map, decomposeOption, customClassNameOption, overriddenNameOption, DoGenerateOption, unfoldOption);
    }

    public static class MessageMockBuilder {
        private String name;
        private String fullName;
        private File containingFile;
        private Message containingType;
        private List<? extends EnumOrMessage> nested$value;
        private boolean nested$set;
        private List<? extends Field> fields$value;
        private boolean fields$set;
        private boolean map$value;
        private boolean map$set;
        private Optional<Boolean> decomposeOption$value;
        private boolean decomposeOption$set;
        private Optional<String> customClassNameOption$value;
        private boolean customClassNameOption$set;
        private Optional<String> overriddenNameOption$value;
        private boolean overriddenNameOption$set;
        private Optional<Boolean> DoGenerateOption$value;
        private boolean DoGenerateOption$set;
        private Optional<Boolean> unfoldOption$value;
        private boolean unfoldOption$set;

        MessageMockBuilder() {
        }

        public MessageMockBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MessageMockBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public MessageMockBuilder containingFile(File containingFile) {
            this.containingFile = containingFile;
            return this;
        }

        public MessageMockBuilder containingType(Message containingType) {
            this.containingType = containingType;
            return this;
        }

        public MessageMockBuilder nested(List<? extends EnumOrMessage> nested) {
            this.nested$value = nested;
            this.nested$set = true;
            return this;
        }

        public MessageMockBuilder fields(List<? extends Field> fields) {
            this.fields$value = fields;
            this.fields$set = true;
            return this;
        }

        public MessageMockBuilder map(boolean map) {
            this.map$value = map;
            this.map$set = true;
            return this;
        }

        public MessageMockBuilder decomposeOption(Optional<Boolean> decomposeOption) {
            this.decomposeOption$value = decomposeOption;
            this.decomposeOption$set = true;
            return this;
        }

        public MessageMockBuilder customClassNameOption(Optional<String> customClassNameOption) {
            this.customClassNameOption$value = customClassNameOption;
            this.customClassNameOption$set = true;
            return this;
        }

        public MessageMockBuilder overriddenNameOption(Optional<String> overriddenNameOption) {
            this.overriddenNameOption$value = overriddenNameOption;
            this.overriddenNameOption$set = true;
            return this;
        }

        public MessageMockBuilder DoGenerateOption(Optional<Boolean> DoGenerateOption) {
            this.DoGenerateOption$value = DoGenerateOption;
            this.DoGenerateOption$set = true;
            return this;
        }

        public MessageMockBuilder unfoldOption(Optional<Boolean> unfoldOption) {
            this.unfoldOption$value = unfoldOption;
            this.unfoldOption$set = true;
            return this;
        }

        public MessageMock build() {
            List<? extends EnumOrMessage> nested$value = this.nested$value;
            if (!this.nested$set) {
                nested$value = MessageMock.$default$nested();
            }
            List<? extends Field> fields$value = this.fields$value;
            if (!this.fields$set) {
                fields$value = MessageMock.$default$fields();
            }
            boolean map$value = this.map$value;
            if (!this.map$set) {
                map$value = MessageMock.$default$map();
            }
            Optional<Boolean> decomposeOption$value = this.decomposeOption$value;
            if (!this.decomposeOption$set) {
                decomposeOption$value = MessageMock.$default$decomposeOption();
            }
            Optional<String> customClassNameOption$value = this.customClassNameOption$value;
            if (!this.customClassNameOption$set) {
                customClassNameOption$value = MessageMock.$default$customClassNameOption();
            }
            Optional<String> overriddenNameOption$value = this.overriddenNameOption$value;
            if (!this.overriddenNameOption$set) {
                overriddenNameOption$value = MessageMock.$default$overriddenNameOption();
            }
            Optional<Boolean> DoGenerateOption$value = this.DoGenerateOption$value;
            if (!this.DoGenerateOption$set) {
                DoGenerateOption$value = MessageMock.$default$DoGenerateOption();
            }
            Optional<Boolean> unfoldOption$value = this.unfoldOption$value;
            if (!this.unfoldOption$set) {
                unfoldOption$value = MessageMock.$default$unfoldOption();
            }
            return new MessageMock(this.name, this.fullName, this.containingFile, this.containingType, nested$value, fields$value, map$value, decomposeOption$value, customClassNameOption$value, overriddenNameOption$value, DoGenerateOption$value, unfoldOption$value);
        }
    }
}
