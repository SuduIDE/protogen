package org.sudu.protogen.test.general;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sudu.protogen.test.TestUtils;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GeneralTest {

    private static final TestUtils utils = new TestUtils("general");

    @Test
    public void task_should_be_as_expected() {
        Class<?> taskClass = Task.class;
        Assertions.assertEquals(String.class, utils.getComponentByName(taskClass, "id").getType());
        Assertions.assertEquals(TaskStatus.class, utils.getComponentByName(taskClass, "status").getType());
        Assertions.assertEquals(Instant.class, utils.getComponentByName(taskClass, "createdAt").getType());
        Assertions.assertEquals(String.class, utils.getComponentByName(taskClass, "context").getType());
        assertThat(taskClass.getMethods())
                .filteredOn(m -> m.getName().equals("fromGrpc"))
                .anyMatch(m -> m.getReturnType().equals(Task.class) && m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(GrpcTask.class));
        assertThat(taskClass.getMethods())
                .filteredOn(m -> m.getName().equals("toGrpc"))
                .anyMatch(m -> m.getReturnType().equals(GrpcTask.class) && m.getParameterTypes().length == 0);
    }

    @Test
    public void fromGrpc_should_set_all_fields() {
        GrpcTask grpcTask = GrpcTask.newBuilder()
                .setId(GrpcTaskId.newBuilder().setUuid("id").build())
                .setStatus(GrpcTaskStatus.CREATED)
                .setCreatedAt(Timestamp.newBuilder().setSeconds(1).build())
                .setContext("context")
                .build();
        Task task = Task.fromGrpc(grpcTask);
        Assertions.assertEquals("id", task.id());
        Assertions.assertEquals(TaskStatus.CREATED, task.status());
        Assertions.assertEquals(Instant.ofEpochSecond(1), task.createdAt());
        Assertions.assertEquals("context", task.context());
    }

    @Test
    public void fromGrpc_makes_optional_fields_nullable_if_absent() {
        GrpcTask grpcTask = GrpcTask.newBuilder()
                .setId(GrpcTaskId.newBuilder().setUuid("uuid").build())
                .setStatus(GrpcTaskStatus.CREATED)
                .setCreatedAt(Timestamp.newBuilder().setSeconds(1).build())
                .build();
        Task task = Task.fromGrpc(grpcTask);
        Assertions.assertNull(task.context());
    }

    @Test
    public void toGrpc_should_set_all_fields() {
        Task task = new Task("uuid", TaskStatus.CREATED, Instant.ofEpochSecond(1), "context");
        GrpcTask grpcTask = task.toGrpc();
        Assertions.assertEquals("uuid", grpcTask.getId().getUuid());
        Assertions.assertEquals(GrpcTaskStatus.CREATED, grpcTask.getStatus());
        Assertions.assertEquals(Timestamp.newBuilder().setSeconds(1).build(), grpcTask.getCreatedAt());
        Assertions.assertEquals("context", grpcTask.getContext());
    }

    @Test
    public void Should_not_throw_on_to_grpc_with_null_field() {
        Task task = new Task("uuid", TaskStatus.CREATED, Instant.EPOCH, null);
        assertDoesNotThrow(task::toGrpc);
    }
}
