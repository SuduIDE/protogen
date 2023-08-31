package org.sudu.protogen.protobuf;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class FileMock extends File {

    public String name;
    public String protoPackage;
    public List<? extends EnumOrMessage> nested;
    public List<? extends Service> services;
    public Optional<Boolean> javaMultipleFilesOption;
    public Optional<String> javaPackageOption;
    public Optional<String> javaOuterClassnameOption;
    public Optional<Boolean> generateOption;
    public Optional<String> protogenPackageOption;
    public Optional<Boolean> disableNotNullOption = Optional.empty();


    public FileMock(
            String name,
            String protoPackage,
            List<? extends EnumOrMessage> nested,
            List<? extends Service> services,
            Optional<Boolean> javaMultipleFilesOption,
            Optional<String> javaPackageOption,
            Optional<String> javaOuterClassnameOption,
            Optional<Boolean> generateOption,
            Optional<String> protogenPackageOption
    ) {
        this.name = name;
        this.protoPackage = protoPackage;
        this.nested = nested;
        this.services = services;
        this.javaMultipleFilesOption = javaMultipleFilesOption;
        this.javaPackageOption = javaPackageOption;
        this.javaOuterClassnameOption = javaOuterClassnameOption;
        this.generateOption = generateOption;
        this.protogenPackageOption = protogenPackageOption;
    }

    public String getName() {
        return this.name;
    }

    public String getProtoPackage() {
        return this.protoPackage;
    }

    public List<? extends EnumOrMessage> getNested() {
        return this.nested;
    }

    public List<? extends Service> getServices() {
        return this.services;
    }

    public Optional<Boolean> getJavaMultipleFilesOption() {
        return this.javaMultipleFilesOption;
    }

    public Optional<String> getJavaPackageOption() {
        return this.javaPackageOption;
    }

    public Optional<String> getJavaOuterClassnameOption() {
        return this.javaOuterClassnameOption;
    }

    public Optional<Boolean> getGenerateOption() {
        return this.generateOption;
    }

    public Optional<String> getProtogenPackageOption() {
        return this.protogenPackageOption;
    }

    @NotNull
    @Override
    public Optional<Boolean> getDisableNotNullOption() {
        return disableNotNullOption;
    }

    @Override
    public String toString() {
        return "FileMock{" +
                "name='" + name + '\'' +
                ", protoPackage='" + protoPackage + '\'' +
                ", nested=" + nested +
                ", services=" + services +
                ", javaMultipleFilesOption=" + javaMultipleFilesOption +
                ", javaPackageOption=" + javaPackageOption +
                ", javaOuterClassnameOption=" + javaOuterClassnameOption +
                ", generateOption=" + generateOption +
                ", protogenPackageOption=" + protogenPackageOption +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMock fileMock = (FileMock) o;
        return Objects.equals(name, fileMock.name) && Objects.equals(protoPackage, fileMock.protoPackage) && Objects.equals(nested, fileMock.nested) && Objects.equals(services, fileMock.services) && Objects.equals(javaMultipleFilesOption, fileMock.javaMultipleFilesOption) && Objects.equals(javaPackageOption, fileMock.javaPackageOption) && Objects.equals(javaOuterClassnameOption, fileMock.javaOuterClassnameOption) && Objects.equals(generateOption, fileMock.generateOption) && Objects.equals(protogenPackageOption, fileMock.protogenPackageOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protoPackage, nested, services, javaMultipleFilesOption, javaPackageOption, javaOuterClassnameOption, generateOption, protogenPackageOption);
    }
}
