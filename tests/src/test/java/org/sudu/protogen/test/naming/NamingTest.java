package org.sudu.protogen.test.naming;

import org.junit.jupiter.api.Test;
import org.sudu.protogen.test.TestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NamingTest {

    private static final TestUtils utils = new TestUtils("naming");

    @Test
    public void Domain_name_omits_grpc_by_default() {
        assertDoesNotThrow(() -> utils.loadDomain("Domain"));
    }

    @Test
    public void Message_name_option_overrides_domain_name() {
        assertDoesNotThrow(() -> utils.loadDomain("NewName"));
        assertThrows(TestUtils.DomainNotFoundError.class, () -> utils.loadDomain("Named"));
    }

    @Test
    public void Field_name_option_overrides_name() {
        Class<?> namedDomain = utils.loadDomain("NewName");
        assertDoesNotThrow(() -> utils.getComponentByName(namedDomain, "namedString"));
        assertThrows(TestUtils.ComponentNotFoundError.class, () -> utils.getComponentByName(namedDomain, "n"));
    }

    @Test
    public void Service_name_should_replace_service_to_client_and_prefix_default() {
        assertDoesNotThrow(() -> utils.loadDomain("DefaultNamingTestClient"));
        assertThrows(TestUtils.DomainNotFoundError.class, () -> utils.loadDomain("NamingTestService"));
    }

    @Test
    public void Method_name_default() {
        Class<?> domain = utils.loadDomain("DefaultNamingTestClient");
        assertThat(domain.getMethods()).anyMatch(m -> m.getName().equals("testMethod"));
    }

    @Test
    public void Service_name_option_overrides_default() {
        assertDoesNotThrow(() -> utils.loadDomain("BaseNamingClient"));
        assertThrows(TestUtils.DomainNotFoundError.class, () -> utils.loadDomain("DefaultNamedServiceClient"));
    }

    @Test
    public void Method_name_option_overrides_default() {
        Class<?> domain = utils.loadDomain("BaseNamingClient");
        assertThat(domain.getMethods()).anyMatch(m -> m.getName().equals("overriddenMethodName"));
        assertThat(domain.getMethods()).noneMatch(m -> m.getName().equals("testMethod"));
    }
}
