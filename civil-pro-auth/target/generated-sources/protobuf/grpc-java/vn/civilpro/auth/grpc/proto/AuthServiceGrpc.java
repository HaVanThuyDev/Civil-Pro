package vn.civilpro.auth.grpc.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: auth.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AuthServiceGrpc {

  private AuthServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "civilpro.auth.AuthService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.LoginRequest,
      vn.civilpro.auth.grpc.proto.AuthResponse> getLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Login",
      requestType = vn.civilpro.auth.grpc.proto.LoginRequest.class,
      responseType = vn.civilpro.auth.grpc.proto.AuthResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.LoginRequest,
      vn.civilpro.auth.grpc.proto.AuthResponse> getLoginMethod() {
    io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.LoginRequest, vn.civilpro.auth.grpc.proto.AuthResponse> getLoginMethod;
    if ((getLoginMethod = AuthServiceGrpc.getLoginMethod) == null) {
      synchronized (AuthServiceGrpc.class) {
        if ((getLoginMethod = AuthServiceGrpc.getLoginMethod) == null) {
          AuthServiceGrpc.getLoginMethod = getLoginMethod =
              io.grpc.MethodDescriptor.<vn.civilpro.auth.grpc.proto.LoginRequest, vn.civilpro.auth.grpc.proto.AuthResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Login"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.AuthResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthServiceMethodDescriptorSupplier("Login"))
              .build();
        }
      }
    }
    return getLoginMethod;
  }

  private static volatile io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.RegisterRequest,
      vn.civilpro.auth.grpc.proto.UserResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = vn.civilpro.auth.grpc.proto.RegisterRequest.class,
      responseType = vn.civilpro.auth.grpc.proto.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.RegisterRequest,
      vn.civilpro.auth.grpc.proto.UserResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.RegisterRequest, vn.civilpro.auth.grpc.proto.UserResponse> getRegisterMethod;
    if ((getRegisterMethod = AuthServiceGrpc.getRegisterMethod) == null) {
      synchronized (AuthServiceGrpc.class) {
        if ((getRegisterMethod = AuthServiceGrpc.getRegisterMethod) == null) {
          AuthServiceGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<vn.civilpro.auth.grpc.proto.RegisterRequest, vn.civilpro.auth.grpc.proto.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthServiceMethodDescriptorSupplier("Register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.RefreshTokenRequest,
      vn.civilpro.auth.grpc.proto.AuthResponse> getRefreshTokenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RefreshToken",
      requestType = vn.civilpro.auth.grpc.proto.RefreshTokenRequest.class,
      responseType = vn.civilpro.auth.grpc.proto.AuthResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.RefreshTokenRequest,
      vn.civilpro.auth.grpc.proto.AuthResponse> getRefreshTokenMethod() {
    io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.RefreshTokenRequest, vn.civilpro.auth.grpc.proto.AuthResponse> getRefreshTokenMethod;
    if ((getRefreshTokenMethod = AuthServiceGrpc.getRefreshTokenMethod) == null) {
      synchronized (AuthServiceGrpc.class) {
        if ((getRefreshTokenMethod = AuthServiceGrpc.getRefreshTokenMethod) == null) {
          AuthServiceGrpc.getRefreshTokenMethod = getRefreshTokenMethod =
              io.grpc.MethodDescriptor.<vn.civilpro.auth.grpc.proto.RefreshTokenRequest, vn.civilpro.auth.grpc.proto.AuthResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RefreshToken"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.RefreshTokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.AuthResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthServiceMethodDescriptorSupplier("RefreshToken"))
              .build();
        }
      }
    }
    return getRefreshTokenMethod;
  }

  private static volatile io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.LogoutRequest,
      vn.civilpro.auth.grpc.proto.LogoutResponse> getLogoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Logout",
      requestType = vn.civilpro.auth.grpc.proto.LogoutRequest.class,
      responseType = vn.civilpro.auth.grpc.proto.LogoutResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.LogoutRequest,
      vn.civilpro.auth.grpc.proto.LogoutResponse> getLogoutMethod() {
    io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.LogoutRequest, vn.civilpro.auth.grpc.proto.LogoutResponse> getLogoutMethod;
    if ((getLogoutMethod = AuthServiceGrpc.getLogoutMethod) == null) {
      synchronized (AuthServiceGrpc.class) {
        if ((getLogoutMethod = AuthServiceGrpc.getLogoutMethod) == null) {
          AuthServiceGrpc.getLogoutMethod = getLogoutMethod =
              io.grpc.MethodDescriptor.<vn.civilpro.auth.grpc.proto.LogoutRequest, vn.civilpro.auth.grpc.proto.LogoutResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Logout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.LogoutRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.LogoutResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthServiceMethodDescriptorSupplier("Logout"))
              .build();
        }
      }
    }
    return getLogoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.ValidateTokenRequest,
      vn.civilpro.auth.grpc.proto.ValidateTokenResponse> getValidateTokenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateToken",
      requestType = vn.civilpro.auth.grpc.proto.ValidateTokenRequest.class,
      responseType = vn.civilpro.auth.grpc.proto.ValidateTokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.ValidateTokenRequest,
      vn.civilpro.auth.grpc.proto.ValidateTokenResponse> getValidateTokenMethod() {
    io.grpc.MethodDescriptor<vn.civilpro.auth.grpc.proto.ValidateTokenRequest, vn.civilpro.auth.grpc.proto.ValidateTokenResponse> getValidateTokenMethod;
    if ((getValidateTokenMethod = AuthServiceGrpc.getValidateTokenMethod) == null) {
      synchronized (AuthServiceGrpc.class) {
        if ((getValidateTokenMethod = AuthServiceGrpc.getValidateTokenMethod) == null) {
          AuthServiceGrpc.getValidateTokenMethod = getValidateTokenMethod =
              io.grpc.MethodDescriptor.<vn.civilpro.auth.grpc.proto.ValidateTokenRequest, vn.civilpro.auth.grpc.proto.ValidateTokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateToken"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.ValidateTokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  vn.civilpro.auth.grpc.proto.ValidateTokenResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AuthServiceMethodDescriptorSupplier("ValidateToken"))
              .build();
        }
      }
    }
    return getValidateTokenMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AuthServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuthServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuthServiceStub>() {
        @java.lang.Override
        public AuthServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuthServiceStub(channel, callOptions);
        }
      };
    return AuthServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AuthServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuthServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuthServiceBlockingStub>() {
        @java.lang.Override
        public AuthServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuthServiceBlockingStub(channel, callOptions);
        }
      };
    return AuthServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AuthServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuthServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuthServiceFutureStub>() {
        @java.lang.Override
        public AuthServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuthServiceFutureStub(channel, callOptions);
        }
      };
    return AuthServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void login(vn.civilpro.auth.grpc.proto.LoginRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLoginMethod(), responseObserver);
    }

    /**
     */
    default void register(vn.civilpro.auth.grpc.proto.RegisterRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    /**
     */
    default void refreshToken(vn.civilpro.auth.grpc.proto.RefreshTokenRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRefreshTokenMethod(), responseObserver);
    }

    /**
     */
    default void logout(vn.civilpro.auth.grpc.proto.LogoutRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.LogoutResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLogoutMethod(), responseObserver);
    }

    /**
     */
    default void validateToken(vn.civilpro.auth.grpc.proto.ValidateTokenRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.ValidateTokenResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateTokenMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AuthService.
   */
  public static abstract class AuthServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AuthServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AuthService.
   */
  public static final class AuthServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AuthServiceStub> {
    private AuthServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuthServiceStub(channel, callOptions);
    }

    /**
     */
    public void login(vn.civilpro.auth.grpc.proto.LoginRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void register(vn.civilpro.auth.grpc.proto.RegisterRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void refreshToken(vn.civilpro.auth.grpc.proto.RefreshTokenRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRefreshTokenMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void logout(vn.civilpro.auth.grpc.proto.LogoutRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.LogoutResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void validateToken(vn.civilpro.auth.grpc.proto.ValidateTokenRequest request,
        io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.ValidateTokenResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateTokenMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AuthService.
   */
  public static final class AuthServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AuthServiceBlockingStub> {
    private AuthServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuthServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public vn.civilpro.auth.grpc.proto.AuthResponse login(vn.civilpro.auth.grpc.proto.LoginRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }

    /**
     */
    public vn.civilpro.auth.grpc.proto.UserResponse register(vn.civilpro.auth.grpc.proto.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public vn.civilpro.auth.grpc.proto.AuthResponse refreshToken(vn.civilpro.auth.grpc.proto.RefreshTokenRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRefreshTokenMethod(), getCallOptions(), request);
    }

    /**
     */
    public vn.civilpro.auth.grpc.proto.LogoutResponse logout(vn.civilpro.auth.grpc.proto.LogoutRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLogoutMethod(), getCallOptions(), request);
    }

    /**
     */
    public vn.civilpro.auth.grpc.proto.ValidateTokenResponse validateToken(vn.civilpro.auth.grpc.proto.ValidateTokenRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateTokenMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AuthService.
   */
  public static final class AuthServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AuthServiceFutureStub> {
    private AuthServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuthServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuthServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<vn.civilpro.auth.grpc.proto.AuthResponse> login(
        vn.civilpro.auth.grpc.proto.LoginRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<vn.civilpro.auth.grpc.proto.UserResponse> register(
        vn.civilpro.auth.grpc.proto.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<vn.civilpro.auth.grpc.proto.AuthResponse> refreshToken(
        vn.civilpro.auth.grpc.proto.RefreshTokenRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRefreshTokenMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<vn.civilpro.auth.grpc.proto.LogoutResponse> logout(
        vn.civilpro.auth.grpc.proto.LogoutRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<vn.civilpro.auth.grpc.proto.ValidateTokenResponse> validateToken(
        vn.civilpro.auth.grpc.proto.ValidateTokenRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateTokenMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LOGIN = 0;
  private static final int METHODID_REGISTER = 1;
  private static final int METHODID_REFRESH_TOKEN = 2;
  private static final int METHODID_LOGOUT = 3;
  private static final int METHODID_VALIDATE_TOKEN = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LOGIN:
          serviceImpl.login((vn.civilpro.auth.grpc.proto.LoginRequest) request,
              (io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse>) responseObserver);
          break;
        case METHODID_REGISTER:
          serviceImpl.register((vn.civilpro.auth.grpc.proto.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.UserResponse>) responseObserver);
          break;
        case METHODID_REFRESH_TOKEN:
          serviceImpl.refreshToken((vn.civilpro.auth.grpc.proto.RefreshTokenRequest) request,
              (io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse>) responseObserver);
          break;
        case METHODID_LOGOUT:
          serviceImpl.logout((vn.civilpro.auth.grpc.proto.LogoutRequest) request,
              (io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.LogoutResponse>) responseObserver);
          break;
        case METHODID_VALIDATE_TOKEN:
          serviceImpl.validateToken((vn.civilpro.auth.grpc.proto.ValidateTokenRequest) request,
              (io.grpc.stub.StreamObserver<vn.civilpro.auth.grpc.proto.ValidateTokenResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getLoginMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              vn.civilpro.auth.grpc.proto.LoginRequest,
              vn.civilpro.auth.grpc.proto.AuthResponse>(
                service, METHODID_LOGIN)))
        .addMethod(
          getRegisterMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              vn.civilpro.auth.grpc.proto.RegisterRequest,
              vn.civilpro.auth.grpc.proto.UserResponse>(
                service, METHODID_REGISTER)))
        .addMethod(
          getRefreshTokenMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              vn.civilpro.auth.grpc.proto.RefreshTokenRequest,
              vn.civilpro.auth.grpc.proto.AuthResponse>(
                service, METHODID_REFRESH_TOKEN)))
        .addMethod(
          getLogoutMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              vn.civilpro.auth.grpc.proto.LogoutRequest,
              vn.civilpro.auth.grpc.proto.LogoutResponse>(
                service, METHODID_LOGOUT)))
        .addMethod(
          getValidateTokenMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              vn.civilpro.auth.grpc.proto.ValidateTokenRequest,
              vn.civilpro.auth.grpc.proto.ValidateTokenResponse>(
                service, METHODID_VALIDATE_TOKEN)))
        .build();
  }

  private static abstract class AuthServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AuthServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return vn.civilpro.auth.grpc.proto.AuthProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AuthService");
    }
  }

  private static final class AuthServiceFileDescriptorSupplier
      extends AuthServiceBaseDescriptorSupplier {
    AuthServiceFileDescriptorSupplier() {}
  }

  private static final class AuthServiceMethodDescriptorSupplier
      extends AuthServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AuthServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AuthServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AuthServiceFileDescriptorSupplier())
              .addMethod(getLoginMethod())
              .addMethod(getRegisterMethod())
              .addMethod(getRefreshTokenMethod())
              .addMethod(getLogoutMethod())
              .addMethod(getValidateTokenMethod())
              .build();
        }
      }
    }
    return result;
  }
}
