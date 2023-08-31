package org.sudu.protogen.test.messageGeneration;

public record CustomImpl(int a) {

    public static CustomImpl fromGrpc(GrpcCustom grpc) {
        return new CustomImpl(grpc.getA());
    }

    public GrpcCustom toGrpc() {
        return GrpcCustom.newBuilder().setA(a).build();
    }
}
