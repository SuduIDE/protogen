package org.sudu.protogen.test.general;

public enum TaskStatus {

    CREATED,
    EXECUTING,
    FINISHED;

    public static TaskStatus fromGrpc(GrpcTaskStatus grpc) {
        switch (grpc) {
            case CREATED:
                return TaskStatus.CREATED;
            case EXECUTING:
                return TaskStatus.EXECUTING;
            case FINISHED:
                return TaskStatus.FINISHED;
        }
        throw new IllegalArgumentException("Enum value is not recognized");
    }

    public GrpcTaskStatus toGrpc() {
        switch (this) {
            case CREATED:
                return GrpcTaskStatus.CREATED;
            case EXECUTING:
                return GrpcTaskStatus.EXECUTING;
            case FINISHED:
                return GrpcTaskStatus.FINISHED;
        }
        throw new IllegalStateException();
    }
}