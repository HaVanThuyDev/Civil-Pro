package vn.civilpro.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.model.dto.request.LoginRequest;
import vn.civilpro.model.dto.request.RefreshTokenRequest;
import vn.civilpro.model.dto.request.RegisterRequest;
import vn.civilpro.service.impl.AuthServiceImpl;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        return ResponseEntity.ok(authService.login(request, getClientIp(http)));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,  @AuthenticationPrincipal UserDetails user) {
        String register = Optional.ofNullable(user).map(UserDetails::getUsername).orElse("SYSTEM");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, register));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header,
                                    @RequestBody(required = false) RefreshTokenRequest request,
                                    @AuthenticationPrincipal UserDetails user) {
        String access = header.startsWith("Bearer ") ? header.substring(7) : header;
        String refresh = Optional.ofNullable(request).map(RefreshTokenRequest::getRefreshToken).orElse(null);
        String username = Optional.ofNullable(user).map(UserDetails::getUsername).orElse(null);
        authService.logout(access, refresh, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(user);
    }

    private String getClientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        return (xf != null && !xf.isBlank()) ? xf.split(",")[0].trim() : req.getRemoteAddr();
    }
}