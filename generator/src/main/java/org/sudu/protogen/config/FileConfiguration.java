package org.sudu.protogen.config;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FileConfiguration {

    @Nullable
    public Boolean enable;

    @Nullable
    public String targetPackage;

    @Nullable
    public Boolean disableNotNull;

    @NotNull
    public Map<String, DescriptorConfiguration> descriptors = new HashMap<>();

    @Override
    public String toString() {
        return "FileConfiguration{" +
                "enable=" + enable +
                ", targetPackage='" + targetPackage + '\'' +
                ", disableNotNull=" + disableNotNull +
                ", descriptors=" + descriptors +
                '}';
    }

    @JsonAnySetter
    public void setOtherField(String name, DescriptorConfiguration value) {
        descriptors.put(name, value);
    }
}
