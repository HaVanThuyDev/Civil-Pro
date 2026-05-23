package vn.civilpro.service;

import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.dto.request.LoginRequest;
import vn.civilpro.model.dto.request.RefreshTokenRequest;
import vn.civilpro.model.dto.request.RegisterRequest;
import vn.civilpro.model.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request, String ipAddress);

    UserDto register(RegisterRequest request, String creator);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken, String refreshToken, String username);
}
