package org.sudu.protogen.descriptors;

import com.squareup.javapoet.ClassName;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.config.naming.NamingManager;
import org.sudu.protogen.utils.Name;

import java.util.List;
import java.util.Optional;

public abstract class EnumOrMessage implements Descriptor {

    public abstract @NotNull String getName();

    public abstract @NotNull String getFullName();

    public abstract @NotNull List<? extends EnumOrMessage> getNested();

    public abstract @NotNull File getContainingFile();

    public abstract @Nullable Message getContainingType();

    public abstract @Nullable String getCustomClass();

    public final boolean isDomain() {
        return doGenerate() || getCustomClass() != null;
    }

    public boolean doGenerate() {
        if (getCustomClass() != null) return false;
        return getDoGenerateOption()
                .orElse(getContainingFile().doEnableGenerator()
                        && !getName().contains("Request")
                        && !getName().contains("Response")
                );
    }

    public ClassName getProtobufTypeName() {
        if (getContainingType() == null) {
            String javaPackage = getContainingFile().getJavaPackage();
            String enclosingClass = getContainingFile().getEnclosingClass();
            String className = enclosingClass == null ? getName() : enclosingClass + "." + getName();
            return ClassName.get(javaPackage, className);
        } else {
            ClassName containing = getContainingType().getProtobufTypeName();
            return ClassName.get(containing.packageName(), containing.simpleName(), getName());
        }
    }

    public ClassName getDomainTypeName(NamingManager namingManager) {
        Validate.validState(isDomain());
        if (getCustomClass() != null) {
            String customClass = getCustomClass();
            return ClassName.get(Name.getPackage(customClass), Name.getLastName(customClass));
        }
        if (getContainingType() == null) {
            String javaPackage = getContainingFile().getGeneratePackage();
            return ClassName.get(javaPackage, getDomainName(namingManager));
        } else {
            ClassName containing = getContainingType().getDomainTypeName(namingManager);
            return ClassName.get(containing.packageName(), containing.simpleName(), getDomainName(namingManager));
        }
    }

    private String getDomainName(NamingManager namingManager) {
        return getOverriddenNameOption().orElseGet(() -> namingManager.manageName(getName()));
    }

    protected abstract Optional<Boolean> getDoGenerateOption();

    protected abstract Optional<String> getOverriddenNameOption();
}
