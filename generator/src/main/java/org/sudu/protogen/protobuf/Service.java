package org.sudu.protogen.protobuf;

import com.squareup.javapoet.ClassName;
import org.sudu.protogen.utils.Name;

import java.util.List;
import java.util.Optional;

public abstract class Service {

    public abstract String getName();

    public abstract File getContainingFile();

    public abstract List<? extends Method> getMethods();

    public final boolean isAbstract() {
        return getAbstractOption().orElse(false);
    }

    public final boolean doGenerate() {
        return getGenerateOption().orElse(getContainingFile().doGenerate());
    }

    public final String generatedName() {
        return getNameOption()
                .orElse("Default" + Name.toCamelCase(getName().replace("Service", "")) + "Client");
    }

    public final ClassName stubClass() {
        String stubTypeName = getName() + "Grpc";
        String packageName = getContainingFile().getJavaPackage();
        return ClassName.get(packageName, stubTypeName);
    }

    public final ClassName blockingStubClass() {
        ClassName stubClass = stubClass();
        return ClassName.get(stubClass.packageName(), stubClass.simpleName() + "." + getName() + "BlockingStub");
    }

    protected abstract Optional<Boolean> getAbstractOption();

    protected abstract Optional<Boolean> getGenerateOption();

    protected abstract Optional<String> getNameOption();
}
