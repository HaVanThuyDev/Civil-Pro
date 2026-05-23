package vn.civilpro.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.civilpro.model.entity.Permission;
import vn.civilpro.model.entity.Role;
import vn.civilpro.model.entity.User;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    @Value("${civil-pro.jwt.secret}")
    private String secret;

    @Value("${civil-pro.jwt.access-token-expiration:86400000}")
    private long accessTokenExpMs;

    @Value("${civil-pro.jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpMs;

    public String generateAccessToken(User user) {
        Set<String> authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());

        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());

        String authoritiesStr = String.join(",", authorities);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId",         user.getId().toString())
                .claim("fullName",       user.getFullName())
                .claim("adminUnitCode",  user.getAdministrativeUnitCode())
                .claim("roles",          roles)
                .claim("authorities",    new ArrayList<>(authorities))
                .claim("authoritiesStr", authoritiesStr)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId",    user.getId().toString())
                .claim("tokenType", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (UnsupportedJwtException e) {
            log.warn("[JWT] Unsupported token", e);
        } catch (MalformedJwtException e) {
            log.warn("[JWT] Token malformed", e);
        } catch (Exception e) {
            log.warn("[JWT] Token invalid: {}", e.getMessage(), e);
        }
        return false;
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public long getRefreshTokenExpMs() {
        return refreshTokenExpMs;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}