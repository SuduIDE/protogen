package org.sudu.protogen.test.extconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExtConfigurationTest {

    @Test
    public void Transformable_should_become_int() throws NoSuchFieldException {
        assertThat(Sum.class.getRecordComponents())
                .filteredOn(c -> c.getName().equals("sum"))
                .filteredOn(c -> c.getType().equals(int.class));
    }

    @Test
    public void Transformable_sums() {
        Transformable transformable = new Transformable(2, -5);
        Sum sum = Sum.fromGrpc(
                GrpcSum.newBuilder()
                        .setSum(transformable.toGrpc())
                        .build()
        );
        Assertions.assertEquals(-3, sum.sum());
    }
}
