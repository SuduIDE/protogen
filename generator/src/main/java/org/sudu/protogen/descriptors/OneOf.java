package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.config.naming.NamingManager;
import org.sudu.protogen.utils.Name;

import java.util.List;
import java.util.Objects;

public class OneOf implements Descriptor {

    private final Descriptors.OneofDescriptor oneofDescriptor;

    private final Message parent;

    public OneOf(Descriptors.OneofDescriptor oneofDescriptor, Message parent) {
        this.oneofDescriptor = oneofDescriptor;
        this.parent = parent;
    }

    @NotNull
    public String getName() {
        return oneofDescriptor.getName();
    }

    @NotNull
    public List<String> getFieldsCases() {
        return oneofDescriptor.getFields().stream()
                .map(Descriptors.FieldDescriptor::getName)
                .map(String::toUpperCase)
                .toList();
    }

    public ClassName getProtobufTypeName() {
        ClassName parentClass = parent.getProtobufTypeName();
        return ClassName.get(parentClass.packageName(), parentClass.simpleName(), Name.toCamelCase(getName()) + "Case");
    }

    public ClassName getDomainTypeName(NamingManager namingManager) {
        ClassName parentClass = parent.getDomainTypeName(namingManager);
        return ClassName.get(parentClass.packageName(), parentClass.simpleName(), Name.toCamelCase(getName()) + "Case");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneOf oneOf = (OneOf) o;
        return Objects.equals(oneofDescriptor, oneOf.oneofDescriptor) && Objects.equals(parent, oneOf.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oneofDescriptor, parent);
    }
}
