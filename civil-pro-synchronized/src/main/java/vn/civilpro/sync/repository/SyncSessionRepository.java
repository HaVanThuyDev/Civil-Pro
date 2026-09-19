package vn.civilpro.sync.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.sync.entity.SyncSession;

import java.util.Optional;

@Repository
public interface SyncSessionRepository extends JpaRepository<SyncSession, Long> {

    Optional<SyncSession> findTopByOrderByStartTimeDesc();

    Optional<SyncSession> findBySessionCode(String sessionCode);

    Page<SyncSession> findAllByOrderByStartTimeDesc(Pageable pageable);
}
