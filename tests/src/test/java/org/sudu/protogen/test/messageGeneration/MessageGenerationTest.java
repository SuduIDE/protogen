package org.sudu.protogen.test.messageGeneration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sudu.protogen.test.TestUtils;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageGenerationTest {

    private static final TestUtils utils = new TestUtils("messageGeneration");

    @Test
    public void Should_generate_all_field() {
        Class<?> domain = utils.loadDomain("Domain");
        Assertions.assertEquals(
                Set.of("a", "b"),
                Arrays.stream(domain.getRecordComponents()).map(RecordComponent::getName).collect(Collectors.toSet())
        );
    }

    @Test
    public void Option_message_gen_false_disables_generation() {
        assertThrows(TestUtils.DomainNotFoundError.class, () -> utils.loadDomain("DontGen"));
    }

    @Test
    public void Should_not_generate_custom_written() {
        assertThrows(TestUtils.DomainNotFoundError.class, () -> utils.loadDomain("Custom"));
    }

    @Test
    public void Should_reference_to_custom_written_in_generated() {
        Class<?> consumer = utils.loadDomain("CustomConsumer");
        Assertions.assertEquals(
                "org.sudu.protogen.test.messageGeneration.CustomImpl",
                consumer.getRecordComponents()[0].getType().getName()
        );
    }

    @Test
    public void Only_unused_message_should_have_empty_domain() {
        Assertions.assertEquals(0, WithOnlyUnusedField.class.getRecordComponents().length);
    }

    @Test
    public void Unused_field_should_be_ignored() {
        RecordComponent[] components = WithUnusedField.class.getRecordComponents();
        assertThat(components)
                .filteredOn(c -> c.getName().equals("b"))
                .isEmpty();
        Assertions.assertEquals(1, components.length);
    }
}
