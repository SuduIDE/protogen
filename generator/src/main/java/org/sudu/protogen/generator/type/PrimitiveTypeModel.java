package org.sudu.protogen.generator.type;

import com.squareup.javapoet.TypeName;

public class PrimitiveTypeModel extends TypeModel {

    private final TypeName typeName;

    public PrimitiveTypeModel(TypeName typeName) {
        super(typeName);
        this.typeName = typeName;
    }

    @Override
    public TypeName getTypeName() {
        return typeName;
    }
}
