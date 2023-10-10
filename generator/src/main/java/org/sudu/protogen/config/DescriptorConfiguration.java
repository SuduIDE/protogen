package org.sudu.protogen.config;

import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.RepeatedContainer;

import javax.lang.model.element.Modifier;
import java.util.Map;

public class DescriptorConfiguration {
    // General
    public @Nullable Boolean generate;
    public @Nullable String name;
    public @Nullable String customClass;

    // Message
    public @Nullable Boolean unfold;
    public @Nullable String comparator;
    public @Nullable String topic;
    public @Nullable Map<String, FieldConfiguration> fields;

    // Enum
    public @Nullable Map<String, EnumValueConfiguration> values;

    // Service
    public @Nullable Boolean isAbstract;
    public @Nullable Map<String, MethodConfiguration> methods;

    public static class EnumValueConfiguration {
        public @Nullable String name;
        public @Nullable Boolean unused;
    }

    public static class FieldConfiguration {
        public @Nullable String name;
        public @Nullable RepeatedContainer container;
        public @Nullable Boolean unused;
    }

    public static class MethodConfiguration {
        public @Nullable Boolean generate;
        public @Nullable String name;
        public @Nullable Boolean unfoldRequest;
        public @Nullable Integer ifNotFound;
        public @Nullable RepeatedContainer container;
        public @Nullable Modifier accessModifier;
    }

    @Override
    public String toString() {
        return "DescriptorConfiguration{" +
                "generate=" + generate +
                ", name='" + name + '\'' +
                ", customClass='" + customClass + '\'' +
                ", unfold=" + unfold +
                ", comparator=" + comparator +
                ", topic='" + topic + '\'' +
                ", fields=" + fields +
                ", values=" + values +
                ", isAbstract=" + isAbstract +
                ", methods=" + methods +
                '}';
    }
}
