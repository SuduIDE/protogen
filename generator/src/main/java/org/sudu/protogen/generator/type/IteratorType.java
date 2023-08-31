package org.sudu.protogen.generator.type;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.Iterator;

public class IteratorType extends TypeModel {

    TypeModel iteratedType;

    public IteratorType(TypeModel iteratedType) {
        super(ParameterizedTypeName.get(
                ClassName.get(Iterator.class),
                iteratedType.getTypeName().box()
        ));
        this.iteratedType = iteratedType;
    }

    public TypeModel getIteratedType() {
        return iteratedType;
    }
}
