package vn.civilpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.civilpro.model.entity.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
