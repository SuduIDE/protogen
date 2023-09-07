package org.sudu.protogen.test.unfolding;

import org.junit.jupiter.api.Test;
import org.sudu.protogen.test.TestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnfoldingTest {

    private static final TestUtils utils = new TestUtils("unfolding");

    @Test
    public void Do_not_generate_domains_for_unfolded() {
        assertThrows(TestUtils.DomainNotFoundError.class, () -> utils.loadDomain("Id"));
    }

    @Test
    public void Generate_domains_for_unfolded_if_specified() {
        assertDoesNotThrow(() -> utils.loadDomain("GeneratedId"));
    }

    @Test
    public void Generated_unfolded_type_unfolds_in_consumer() {
        Class<?> consumer = utils.loadDomain("GeneratedIdConsumer");
        assertThat(consumer.getRecordComponents())
                .anyMatch(c -> c.getType().getName().equals("java.lang.String"));
        assertThat(consumer.getRecordComponents())
                .noneMatch(c -> c.getType().getName().equals("org.sudu.protogen.test.unfolding.GeneratedId"));
    }

    @Test
    public void Unfold_option_works_for_all_types() {
        Class<?> domain = utils.loadDomain("Domain");
        utils.getComponentByName(domain, "id");
        utils.getComponentByName(domain, "optionalId");
        utils.getComponentByName(domain, "repeatedId");
        utils.getComponentByName(domain, "namedId");
    }

    @Test
    public void Unfold_option_unwraps_nullability() {
        Class<?> domain = utils.loadDomain("Domain");
        assertThat(utils.getComponentByName(domain, "id"))
                .matches(c -> c.getType().isPrimitive()); // i.e. notnull
        assertThat(utils.getComponentByName(domain, "optionalId"))
                .matches(c -> !c.getType().isPrimitive()); // i.e. nullable

        Class<?> optionalDomain = utils.loadDomain("OptionalDomain");
        assertThat(utils.getComponentByName(optionalDomain, "id"))
                .matches(c -> !c.getType().isPrimitive()); // i.e. nullable
        assertThat(utils.getComponentByName(optionalDomain, "optionalId"))
                .matches(c -> !c.getType().isPrimitive()); // i.e. nullable
    }

    @Test
    public void Field_name_overrides_unfolded_name() {
        Class<?> namedDomain = utils.loadDomain("NamedDomain");
        utils.getComponentByName(namedDomain, "id1");
        utils.getComponentByName(namedDomain, "id2");
        utils.getComponentByName(namedDomain, "id3");
        utils.getComponentByName(namedDomain, "id4");
        assertThrows(TestUtils.ComponentNotFoundError.class, () -> utils.getComponentByName(namedDomain, "id"));
        assertThrows(TestUtils.ComponentNotFoundError.class, () -> utils.getComponentByName(namedDomain, "optionalId"));
        assertThrows(TestUtils.ComponentNotFoundError.class, () -> utils.getComponentByName(namedDomain, "repeatedId"));
        assertThrows(TestUtils.ComponentNotFoundError.class, () -> utils.getComponentByName(namedDomain, "namedId"));
    }

}
