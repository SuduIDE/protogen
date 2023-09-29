package org.sudu.protogen.test;

import io.grpc.Channel;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.test.client.DefaultSomeClient;
import org.sudu.protogen.test.client.Domain;

public class A extends DefaultSomeClient {

    public A(Channel channel) {
        super(channel);
    }

    @Override
    public @Nullable Domain commonMultiNullable(String r1, String r2) {
        return super.commonMultiNullable(r1, r2);
    }
}
