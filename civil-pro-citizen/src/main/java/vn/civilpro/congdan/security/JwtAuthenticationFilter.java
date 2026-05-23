package vn.civilpro.congdan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ================================================================
 * JWT AUTHENTICATION FILTER
 * Chạy 1 lần mỗi request (OncePerRequestFilter).
 * Đọc JWT từ header Authorization, validate và set SecurityContext.
 *
 * Token do Auth Service cấp, các service khác chỉ VALIDATE - không cấp mới.
 * Gateway đã validate trước, nhưng mỗi service vẫn validate lại (defense in depth).
 * ================================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${civil-pro.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = parseToken(token);

            String username = claims.getSubject();

            // Lấy danh sách quyền từ claim "authorities"
            @SuppressWarnings("unchecked")
            List<String> authorities = claims.get("authorities", List.class);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> grantedAuthorities = authorities == null
                        ? List.of()
                        : authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Đưa username vào MDC để log có context
                org.slf4j.MDC.put("username", username);
            }

        } catch (Exception e) {
            log.warn("[JWT] Token không hợp lệ: {}", e.getMessage());
            // Không set authentication → Spring Security sẽ trả 401
        }

        filterChain.doFilter(request, response);
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}