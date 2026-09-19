package vn.civilpro.grpc;

import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.civilpro.auth.grpc.proto.*;
import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.dto.request.LoginRequest;
import vn.civilpro.model.dto.request.RefreshTokenRequest;
import vn.civilpro.model.dto.request.RegisterRequest;
import vn.civilpro.model.dto.response.AuthResponse;
import vn.civilpro.model.entity.Role;
import vn.civilpro.security.JwtService;
import vn.civilpro.service.impl.AuthServiceImpl;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthGrpcService Unit Tests - Model <-> gRPC Mapping")
class AuthGrpcServiceTest {

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private StreamObserver<vn.civilpro.auth.grpc.proto.AuthResponse> authResponseObserver;

    @Mock
    private StreamObserver<UserResponse> userResponseObserver;

    @Mock
    private StreamObserver<LogoutResponse> logoutResponseObserver;

    @Mock
    private StreamObserver<ValidateTokenResponse> validateTokenObserver;

    @InjectMocks
    private AuthGrpcService authGrpcService;

    private AuthResponse sampleAuthResponse;

    @BeforeEach
    void setUp() {
        sampleAuthResponse = AuthResponse.builder()
                .userId(1L)
                .username("admin")
                .fullName("System Administrator")
                .administrativeUnitCode("HN-001")
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                .authorities(Set.of("USER:READ", "USER:WRITE"))
                .build();
    }

    @Test
    @DisplayName("login - Map thành công AuthResponse Model sang gRPC AuthResponse Proto")
    void login_success_mapsAllFields() {
        when(authService.login(any(LoginRequest.class), any())).thenReturn(sampleAuthResponse);

        vn.civilpro.auth.grpc.proto.LoginRequest request = vn.civilpro.auth.grpc.proto.LoginRequest.newBuilder()
                .setLoginName("admin")
                .setPassword("password123")
                .setClientIp("127.0.0.1")
                .build();

        authGrpcService.login(request, authResponseObserver);

        ArgumentCaptor<vn.civilpro.auth.grpc.proto.AuthResponse> captor =
                ArgumentCaptor.forClass(vn.civilpro.auth.grpc.proto.AuthResponse.class);
        verify(authResponseObserver).onNext(captor.capture());
        verify(authResponseObserver).onCompleted();

        vn.civilpro.auth.grpc.proto.AuthResponse proto = captor.getValue();
        assertThat(proto.getAccessToken()).isEqualTo("mock-access-token");
        assertThat(proto.getRefreshToken()).isEqualTo("mock-refresh-token");
        assertThat(proto.getTokenType()).isEqualTo("Bearer");
        assertThat(proto.getExpiresIn()).isEqualTo(86400000L);

        UserInfo user = proto.getUser();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getFullName()).isEqualTo("System Administrator");
        assertThat(user.getAdministrativeCode()).isEqualTo("HN-001");
        assertThat(user.getRoleCodesList()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(user.getAuthoritiesList()).containsExactlyInAnyOrder("USER:READ", "USER:WRITE");
    }

    @Test
    @DisplayName("refreshToken - Map thành công kết quả làm mới token sang gRPC")
    void refreshToken_success() {
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(sampleAuthResponse);

        vn.civilpro.auth.grpc.proto.RefreshTokenRequest request = vn.civilpro.auth.grpc.proto.RefreshTokenRequest.newBuilder()
                .setRefreshToken("mock-refresh-token")
                .build();

        authGrpcService.refreshToken(request, authResponseObserver);

        ArgumentCaptor<vn.civilpro.auth.grpc.proto.AuthResponse> captor =
                ArgumentCaptor.forClass(vn.civilpro.auth.grpc.proto.AuthResponse.class);
        verify(authResponseObserver).onNext(captor.capture());
        verify(authResponseObserver).onCompleted();

        assertThat(captor.getValue().getAccessToken()).isEqualTo("mock-access-token");
    }

    @Test
    @DisplayName("register - Map thành công UserDto sang UserResponse Proto")
    void register_success() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .username("newuser")
                .fullName("New User")
                .email("newuser@example.com")
                .phoneNumber("0987654321")
                .administrativeUnitCode("HN-002")
                .status(1)
                .roles(Set.of(Role.builder().roleCode("ROLE_USER").build()))
                .authorities(Set.of("USER:READ"))
                .build();

        when(authService.register(any(RegisterRequest.class), any())).thenReturn(userDto);

        vn.civilpro.auth.grpc.proto.RegisterRequest request = vn.civilpro.auth.grpc.proto.RegisterRequest.newBuilder()
                .setUsername("newuser")
                .setPassword("secret")
                .setEmail("newuser@example.com")
                .setFullName("New User")
                .setAdministrativeCode("HN-002")
                .addRoleIds("ROLE_USER")
                .build();

        authGrpcService.register(request, userResponseObserver);

        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(userResponseObserver).onNext(captor.capture());
        verify(userResponseObserver).onCompleted();

        UserResponse response = captor.getValue();
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getUser().getUsername()).isEqualTo("newuser");
        assertThat(response.getUser().getEmail()).isEqualTo("newuser@example.com");
        assertThat(response.getUser().getRoleCodesList()).contains("ROLE_USER");
    }

    @Test
    @DisplayName("logout - Trả về LogoutResponse thành công")
    void logout_success() {
        doNothing().when(authService).logout(any(), any(), any());

        vn.civilpro.auth.grpc.proto.LogoutRequest request = vn.civilpro.auth.grpc.proto.LogoutRequest.newBuilder()
                .setAccessToken("token")
                .setRefreshToken("refresh")
                .setUsername("admin")
                .build();

        authGrpcService.logout(request, logoutResponseObserver);

        ArgumentCaptor<LogoutResponse> captor = ArgumentCaptor.forClass(LogoutResponse.class);
        verify(logoutResponseObserver).onNext(captor.capture());
        verify(logoutResponseObserver).onCompleted();

        assertThat(captor.getValue().getSuccess()).isTrue();
    }

    @Test
    @DisplayName("validateToken - Trả về hợp lệ với token đúng")
    void validateToken_validToken_returnsTrue() {
        Claims claims = mock(Claims.class);
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.parseClaims("valid-token")).thenReturn(claims);
        when(claims.get("userId", String.class)).thenReturn("1");
        when(claims.getSubject()).thenReturn("admin");
        doReturn(List.of("ROLE_ADMIN")).when(claims).get("roles", List.class);

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken("valid-token").build();
        authGrpcService.validateToken(request, validateTokenObserver);

        ArgumentCaptor<ValidateTokenResponse> captor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(validateTokenObserver).onNext(captor.capture());
        verify(validateTokenObserver).onCompleted();

        ValidateTokenResponse response = captor.getValue();
        assertThat(response.getValid()).isTrue();
        assertThat(response.getUserId()).isEqualTo("1");
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRolesList()).contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("validateToken - Trả về không hợp lệ với token sai")
    void validateToken_invalidToken_returnsFalse() {
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken("invalid-token").build();
        authGrpcService.validateToken(request, validateTokenObserver);

        ArgumentCaptor<ValidateTokenResponse> captor = ArgumentCaptor.forClass(ValidateTokenResponse.class);
        verify(validateTokenObserver).onNext(captor.capture());
        verify(validateTokenObserver).onCompleted();

        ValidateTokenResponse response = captor.getValue();
        assertThat(response.getValid()).isFalse();
    }
}