package org.sudu.protogen.generator;

import com.squareup.javapoet.TypeSpec;
import org.sudu.protogen.descriptors.Enum;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.enumeration.EnumGenerator;
import org.sudu.protogen.generator.message.MessageGenerator;

public class EnumOrMessageGenerator {

    private final GenerationContext context;

    private final EnumOrMessage descriptor;

    public EnumOrMessageGenerator(GenerationContext context, EnumOrMessage descriptor) {
        this.context = context;
        this.descriptor = descriptor;
    }

    public TypeSpec generate() {
        if (descriptor instanceof Message msg) {
            return new MessageGenerator(context, msg).generate();
        }
        if (descriptor instanceof Enum en) {
            return new EnumGenerator(context, en).generate();
        }
        throw new IllegalStateException();
    }
}
