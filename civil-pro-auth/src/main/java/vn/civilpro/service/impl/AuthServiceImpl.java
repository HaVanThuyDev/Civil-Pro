package vn.civilpro.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.dto.request.LoginRequest;
import vn.civilpro.model.dto.request.RefreshTokenRequest;
import vn.civilpro.model.dto.request.RegisterRequest;
import vn.civilpro.model.dto.response.AuthResponse;
import vn.civilpro.model.entity.*;
import vn.civilpro.repository.*;
import vn.civilpro.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SystemLogRepository systemLogRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${civil-pro.security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${civil-pro.security.lockout-duration-minutes:30}")
    private int lockoutMinutes;

    // Blacklist token trong memory (tạm thời thay Redis)
    private final Map<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    @Transactional
    public AuthResponse login(LoginRequest request, String ip) {
        String username = request.getUsername();

        // Kiểm tra user tồn tại
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> handleLoginFail(username, "INVALID_CREDENTIALS", ip));

        // Kiểm tra account bị khóa vĩnh viễn
        if (user.getStatus() == 0) {
            throw handleLoginFail(username, "ACCOUNT_LOCKED", ip);
        }

        // Lấy hoặc tạo mới login attempt
        LoginAttempt attempt = loginAttemptRepository.findByUsername(username)
                .orElse(LoginAttempt.builder()
                        .username(username)
                        .attemptCount(0)
                        .lastAttemptAt(LocalDateTime.now())
                        .build());

        // Kiểm tra có bị lock tạm thời không
        if (attempt.isLocked()) {
            throw handleLoginFail(username, "ACCOUNT_TEMPORARILY_LOCKED", ip);
        }

        // Kiểm tra vượt quá số lần thử
        if (attempt.getAttemptCount() >= maxLoginAttempts) {
            attempt.lock(lockoutMinutes);
            loginAttemptRepository.save(attempt);

            // Khóa vĩnh viễn
            user.setStatus(0);
            userRepository.save(user);

            throw handleLoginFail(username, "ACCOUNT_LOCKED_MAX_FAILS", ip);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            attempt.incrementAttempt();
            attempt.setIpAddress(ip);
            loginAttemptRepository.save(attempt);

            throw handleLoginFail(username, "INVALID_CREDENTIALS", ip);
        }

        attempt.reset();
        loginAttemptRepository.save(attempt);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refresh, ip);
        saveLog(SystemLog.info(username, "AUTH", "LOGIN_SUCCESS", ip));

        return buildAuthResponse(user, access, refresh);
    }

    @Transactional
    public UserDto register(RegisterRequest request, String creator) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("USERNAME_EXISTS");
        }

        Set<Role> roles = Optional.ofNullable(request.getRoleCodes())
                .orElse(Set.of())
                .stream()
                .map(code -> roleRepository.findByRoleCode(code)
                        .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND")))
                .collect(Collectors.toSet());

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .administrativeUnitCode(request.getAdministrativeUnitCode())
                .status(1)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);
        saveLog(SystemLog.info(creator, "AUTH", "USER_CREATED: " + saved.getUsername(), null));

        return toUserDto(saved);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("TOKEN_INVALID");
        }

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hashToken(token))
                .filter(RefreshToken::isValid)
                .orElseThrow(() -> new RuntimeException("TOKEN_EXPIRED_OR_REVOKED"));

        User user = stored.getUser();

        // Revoke old refresh token
        stored.revoke();
        refreshTokenRepository.save(stored);

        // Generate new tokens
        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, newRefresh, null);

        return buildAuthResponse(user, newAccess, newRefresh);
    }

    @Transactional
    public void logout(String access, String refresh, String username) {
        // Revoke refresh token
        Optional.ofNullable(refresh).ifPresent(r ->
                refreshTokenRepository.findByTokenHash(hashToken(r))
                        .ifPresent(t -> {
                            t.revoke();
                            refreshTokenRepository.save(t);
                        })
        );

        // Blacklist access token
        Optional.ofNullable(access).ifPresent(this::blacklistToken);

        saveLog(SystemLog.info(username, "AUTH", "LOGOUT_SUCCESS", null));
    }

    private void blacklistToken(String token) {
        try {
            long expTime = jwtService.extractExpiration(token).getTime();
            tokenBlacklist.put(token, expTime);
            log.debug("Token blacklisted until: {}", new Date(expTime));
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        Long expTime = tokenBlacklist.get(token);
        if (expTime != null) {
            if (System.currentTimeMillis() > expTime) {
                tokenBlacklist.remove(token); // Xóa token đã hết hạn
                return false;
            }
            return true;
        }
        return false;
    }

    private void saveRefreshToken(User user, String raw, String ip) {
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(raw))
                .expiryAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpMs() / 1000))
                .ipAddress(ip)
                .isRevoked(false)
                .build());
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HASH_ERROR");
        }
    }

    private RuntimeException handleLoginFail(String user, String msg, String ip) {
        saveLog(SystemLog.security(user, msg, ip));
        return new RuntimeException(msg);
    }

    private void saveLog(SystemLog log) {
        try {
            systemLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to save system log", e);
        }
    }

    private AuthResponse buildAuthResponse(User u, String at, String rt) {
        return AuthResponse.builder()
                .accessToken(at)
                .refreshToken(rt)
                .tokenType("Bearer")
                .expiresIn(jwtService.getRefreshTokenExpMs() / 1000)
                .userId(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .administrativeUnitCode(u.getAdministrativeUnitCode())
                .roles(extractRoles(u))
                .authorities(extractPerms(u))
                .build();
    }

    private UserDto toUserDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .administrativeUnitCode(u.getAdministrativeUnitCode())
                .status(u.getStatus())
                .roles(extractRoles(u))
                .authorities(extractPerms(u))
                .createdAt(u.getCreatedAt())
                .lastLoginAt(u.getLastLoginAt())
                .build();
    }

    private Set<String> extractRoles(User u) {
        return u.getRoles().stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());
    }

    private Set<String> extractPerms(User u) {
        return u.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }
}