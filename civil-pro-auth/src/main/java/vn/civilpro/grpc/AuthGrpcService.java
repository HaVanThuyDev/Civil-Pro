package vn.civilpro.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.civilpro.auth.grpc.proto.AuthServiceGrpc;
import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.dto.request.LoginRequest;
import vn.civilpro.model.dto.request.RefreshTokenRequest;
import vn.civilpro.model.dto.request.RegisterRequest;
import vn.civilpro.model.dto.response.AuthResponse;
import vn.civilpro.service.impl.AuthServiceImpl;
import java.util.HashSet;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthServiceImpl authService;

    @Override
    public void login(vn.civilpro.auth.grpc.proto.LoginRequest request, StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> responseObserver) {
        try {
            LoginRequest dto = LoginRequest.builder().username(request.getLoginName()).password(request.getPassword()).build();
            AuthResponse result = authService.login(dto, emptyToNull(request.getClientIp()));
            responseObserver.onNext(toAuthResponse(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, Status.UNAUTHENTICATED);
        }
    }

    @Override
    public void refreshToken(vn.civilpro.auth.grpc.proto.RefreshTokenRequest request, StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> responseObserver) {
        try {
            RefreshTokenRequest dto = RefreshTokenRequest.builder().refreshToken(request.getRefreshToken()).build();
            AuthResponse result = authService.refreshToken(dto);
            responseObserver.onNext(toAuthResponse(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, Status.UNAUTHENTICATED);
        }
    }

    @Override
    public void logout(vn.civilpro.auth.grpc.proto.LogoutRequest request, StreamObserver<vn.civilpro.auth.grpc.proto.LogoutResponse> responseObserver) {
        try {
            authService.logout(
                    request.getAccessToken(),
                    emptyToNull(request.getRefreshToken()),
                    emptyToNull(request.getUsername())
            );
            responseObserver.onNext(vn.civilpro.auth.grpc.proto.LogoutResponse.newBuilder().setSuccess(true).setMessage("Logout success").build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, Status.INTERNAL);
        }
    }

    @Override
    public void register(vn.civilpro.auth.grpc.proto.RegisterRequest request, StreamObserver<vn.civilpro.auth.grpc.proto.UserResponse> responseObserver) {
        try {
            RegisterRequest dto = RegisterRequest.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .administrativeUnitCode(emptyToNull(request.getAdministrativeCode()))
                    .roleCodes(new HashSet<>(request.getRoleIdsList()))
                    .build();
            UserDto result = authService.register(dto, emptyToNull(request.getCreatedBy()));
            responseObserver.onNext(
                    vn.civilpro.auth.grpc.proto.UserResponse.newBuilder()
                            .setStatus(201)
                            .setMessage("User created successfully")
                            .setUser(toUserInfo(result))
                            .build()
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, Status.ALREADY_EXISTS);
        }
    }

    private vn.civilpro.auth.grpc.proto.AuthResponse toAuthResponse(AuthResponse dto) {
        vn.civilpro.auth.grpc.proto.AuthResponse.Builder builder =
                vn.civilpro.auth.grpc.proto.AuthResponse.newBuilder()
                        .setAccessToken(nullSafe(dto.getAccessToken()))
                        .setRefreshToken(nullSafe(dto.getRefreshToken()))
                        .setExpiresIn(dto.getExpiresIn() == null ? 0L : dto.getExpiresIn())
                        .setTokenType(nullSafe(dto.getTokenType()));
        UserDto user = dtoToUser(dto);
        if (user != null) {
            builder.setUser(toUserInfo(user));
        }
        return builder.build();
    }

    private UserDto dtoToUser(AuthResponse dto) {
        if (dto == null) {
            return null;
        }

        return UserDto.builder()
                .id(dto.getUserId())
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .administrativeUnitCode(dto.getAdministrativeUnitCode())
                .roles(dto.getRoles())
                .authorities(dto.getAuthorities())
                .build();
    }
    private vn.civilpro.auth.grpc.proto.UserInfo toUserInfo(UserDto dto) {
        if (dto == null) {
            return vn.civilpro.auth.grpc.proto.UserInfo.getDefaultInstance();
        }

        vn.civilpro.auth.grpc.proto.UserInfo.Builder builder =
                vn.civilpro.auth.grpc.proto.UserInfo.newBuilder()
                        .setId(dto.getId() == null ? 0L : dto.getId())
                        .setUsername(nullSafe(dto.getUsername()))
                        .setFullName(nullSafe(dto.getFullName()))
                        .setEmail(nullSafe(dto.getEmail()))
                        .setPhoneNumber(nullSafe(dto.getPhoneNumber()))
                        .setAdministrativeCode(nullSafe(dto.getAdministrativeUnitCode()))
                        .setStatus(dto.getStatus() == null ? 0 : dto.getStatus());

        if (dto.getRoles() != null) {
            dto.getRoles().forEach(r -> builder.addRoleCodes(String.valueOf(r)));
        }

        if (dto.getAuthorities() != null) {
            dto.getAuthorities().forEach(a -> builder.addAuthorities(String.valueOf(a)));
        }

        return builder.build();
    }

    private <T> void handleError(StreamObserver<T> observer, Exception e, Status status) {
        observer.onError(status.withDescription(nullSafe(e.getMessage())).asRuntimeException());
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
