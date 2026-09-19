package vn.civilpro.pay.grpc.interceptor;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;

@GrpcGlobalClientInterceptor
public class InternalServiceAuthClientInterceptor implements ClientInterceptor {

    public static final Metadata.Key<String> TOKEN_HEADER_KEY =
            Metadata.Key.of("x-internal-service-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final String INTERNAL_SERVICE_SECRET = "civilpro-internal-secret-token-key-2026";

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(TOKEN_HEADER_KEY, INTERNAL_SERVICE_SECRET);
                super.start(responseListener, headers);
            }
        };
    }
}
