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
import vn.civilpro.model.entity.Role;
import vn.civilpro.security.JwtService;
import vn.civilpro.service.impl.AuthServiceImpl;
import io.jsonwebtoken.Claims;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthServiceImpl authService;
    private final JwtService jwtService;

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
            authService.logout(request.getAccessToken(), emptyToNull(request.getRefreshToken()), emptyToNull(request.getUsername()));
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
            responseObserver.onNext(vn.civilpro.auth.grpc.proto.UserResponse.newBuilder()
                    .setStatus(201)
                    .setMessage("User created successfully")
                    .setUser(toUserInfo(result))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, Status.ALREADY_EXISTS);
        }
    }

    @Override
    public void validateToken(vn.civilpro.auth.grpc.proto.ValidateTokenRequest request,
                              StreamObserver<vn.civilpro.auth.grpc.proto.ValidateTokenResponse> responseObserver) {
        try {
            String token = request.getToken();
            if (token != null && !token.isBlank() && jwtService.isTokenValid(token)) {
                Claims claims = jwtService.parseClaims(token);
                String userId = claims.get("userId", String.class);
                String username = claims.getSubject();
                List<?> rolesRaw = claims.get("roles", List.class);
                List<String> roles = rolesRaw != null
                        ? rolesRaw.stream().filter(Objects::nonNull).map(Object::toString).toList()
                        : List.of();

                responseObserver.onNext(vn.civilpro.auth.grpc.proto.ValidateTokenResponse.newBuilder()
                        .setValid(true)
                        .setUserId(userId != null ? userId : "")
                        .setUsername(username != null ? username : "")
                        .addAllRoles(roles)
                        .build());
            } else {
                responseObserver.onNext(vn.civilpro.auth.grpc.proto.ValidateTokenResponse.newBuilder()
                        .setValid(false)
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.warn("[AuthGrpcService] validateToken error: {}", e.getMessage());
            responseObserver.onNext(vn.civilpro.auth.grpc.proto.ValidateTokenResponse.newBuilder()
                    .setValid(false)
                    .build());
            responseObserver.onCompleted();
        }
    }

    private vn.civilpro.auth.grpc.proto.AuthResponse toAuthResponse(AuthResponse dto) {
        vn.civilpro.auth.grpc.proto.AuthResponse.Builder builder = vn.civilpro.auth.grpc.proto.AuthResponse.newBuilder()
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

    // ĐÃ SỬA LỖI: Ép kiểu thô bạo Set<String> sang Set<Role> bằng cách map đối tượng gọn gàng
    private UserDto dtoToUser(AuthResponse dto) {
        if (dto == null) return null;

        return UserDto.builder()
                .id(dto.getUserId())
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .administrativeUnitCode(dto.getAdministrativeUnitCode())
                .roles(dto.getRoles() == null ? null : dto.getRoles().stream()
                        .filter(Objects::nonNull)
                        .map(code -> Role.builder().roleCode(code).build())
                        .collect(java.util.stream.Collectors.toSet()))
                .authorities(dto.getAuthorities())
                .build();
    }

    // ĐÃ VIẾT GỌN & SỬA LỖI: Lấy trực tiếp getRoleCode() và dùng addAll của gRPC cực tối ưu
    private vn.civilpro.auth.grpc.proto.UserInfo toUserInfo(UserDto dto) {
        if (dto == null) return vn.civilpro.auth.grpc.proto.UserInfo.getDefaultInstance();

        vn.civilpro.auth.grpc.proto.UserInfo.Builder builder = vn.civilpro.auth.grpc.proto.UserInfo.newBuilder()
                .setId(dto.getId() == null ? 0L : dto.getId())
                .setUsername(nullSafe(dto.getUsername()))
                .setFullName(nullSafe(dto.getFullName()))
                .setEmail(nullSafe(dto.getEmail()))
                .setPhoneNumber(nullSafe(dto.getPhoneNumber()))
                .setAdministrativeCode(nullSafe(dto.getAdministrativeUnitCode()))
                .setStatus(dto.getStatus() == null ? 0 : dto.getStatus());

        if (dto.getRoles() != null) {
            builder.addAllRoleCodes(dto.getRoles().stream()
                    .filter(r -> r != null && r.getRoleCode() != null)
                    .map(Role::getRoleCode)
                    .toList());
        }

        if (dto.getAuthorities() != null) {
            builder.addAllAuthorities(dto.getAuthorities().stream()
                    .filter(Objects::nonNull)
                    .toList());
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