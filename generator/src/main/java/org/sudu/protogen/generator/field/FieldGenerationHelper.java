package org.sudu.protogen.generator.field;

import com.squareup.javapoet.ParameterSpec;
import org.sudu.protogen.descriptors.Message;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.utils.Poem;

import java.util.List;
import java.util.stream.Stream;

public class FieldGenerationHelper {

    public static Stream<FieldProcessingResult> processAllFields(Message message, GenerationContext context) {
        return message.getFields().stream()
                .map(field -> context.generatorsHolder().generate(field))
                .filter(FieldProcessingResult::isNonVoid);
    }

    public static List<ParameterSpec> processFieldsToParameters(Message message, GenerationContext context) {
        return processAllFields(message, context)
                .map(FieldProcessingResult::field)
                .map(Poem::fieldToParameter)
                .toList();
    }
}
