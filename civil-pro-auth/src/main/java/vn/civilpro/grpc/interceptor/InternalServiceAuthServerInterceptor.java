package vn.civilpro.grpc.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

import java.util.Set;

@Slf4j
@GrpcGlobalServerInterceptor
public class InternalServiceAuthServerInterceptor implements ServerInterceptor {

    public static final Metadata.Key<String> TOKEN_HEADER_KEY =
            Metadata.Key.of("x-internal-service-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final String INTERNAL_SERVICE_SECRET = "civilpro-internal-secret-token-key-2026";

    private static final Set<String> PUBLIC_METHODS = Set.of(
            "civilpro.auth.AuthService/Login",
            "civilpro.auth.AuthService/Register",
            "civilpro.auth.AuthService/RefreshToken"
    );

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();

        // Allow public authentication calls
        if (PUBLIC_METHODS.contains(methodName)) {
            return next.startCall(call, headers);
        }

        String token = headers.get(TOKEN_HEADER_KEY);
        if (token == null || !INTERNAL_SERVICE_SECRET.equals(token)) {
            log.warn("[gRPC Security] Rejected unauthorized auth service call to: {} from: {}",
                    methodName,
                    call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
            call.close(Status.UNAUTHENTICATED.withDescription("Forbidden: Invalid or missing internal service token"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        return next.startCall(call, headers);
    }
}
