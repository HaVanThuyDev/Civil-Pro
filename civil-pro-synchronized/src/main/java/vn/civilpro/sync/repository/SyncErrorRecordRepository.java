package vn.civilpro.sync.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.sync.entity.SyncErrorRecord;

import java.util.List;

@Repository
public interface SyncErrorRecordRepository extends JpaRepository<SyncErrorRecord, Long> {

    List<SyncErrorRecord> findBySessionId(Long sessionId);

    Page<SyncErrorRecord> findBySessionIdAndProcessed(Long sessionId, Boolean processed, Pageable pageable);

    Page<SyncErrorRecord> findBySessionId(Long sessionId, Pageable pageable);
}
