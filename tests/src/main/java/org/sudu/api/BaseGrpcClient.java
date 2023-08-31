package org.sudu.api;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public abstract class BaseGrpcClient implements AutoCloseable {

    // Set max 1Gb size for gRPC message
    private static final int maxInboundMessageSizeInBytes = 1024 * 1024 * 1024;

    protected final ManagedChannel channel;

    public BaseGrpcClient(String host, int port) {
        channel = setGrpcExecutor(configureChannelBuilder(ManagedChannelBuilder.forAddress(host, port))).build();
    }

    public BaseGrpcClient(String endpoint) {
        channel = setGrpcExecutor(configureChannelBuilder(ManagedChannelBuilder.forTarget(endpoint))).build();
    }

    protected BaseGrpcClient(ManagedChannel channel) {
        this.channel = channel;
    }

    protected static ManagedChannelBuilder<?> configureChannelBuilder(ManagedChannelBuilder<?> channelBuilder) {
        return channelBuilder.usePlaintext().maxInboundMessageSize(maxInboundMessageSizeInBytes);
    }

    protected static <T> T nullifyIfNotFound(@NotNull Supplier<T> block) {
        try {
            return block.get();
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
                return null;
            }
            throw ex;
        }
    }

    protected static <T> Collection<T> emptyIfNotFound(@NotNull Supplier<Collection<T>> block) {
        try {
            return block.get();
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
                return List.of();
            }
            throw ex;
        }
    }

    // Can be overridden in subclasses to set custom executor
    protected Executor gprcExecutor() {
        return null;
    }

    private ManagedChannelBuilder<?> setGrpcExecutor(ManagedChannelBuilder<?> channelBuilder) {
        Executor executor = gprcExecutor();
        if (executor != null) {
            channelBuilder.executor(executor);
        }
        return channelBuilder;
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
