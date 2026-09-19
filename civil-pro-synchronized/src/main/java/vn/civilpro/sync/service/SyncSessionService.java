package vn.civilpro.sync.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.sync.entity.SyncErrorRecord;
import vn.civilpro.sync.entity.SyncSession;

import java.util.Optional;

public interface SyncSessionService {

    SyncSession triggerSync(String syncType);

    Optional<SyncSession> getLatestSession();

    Page<SyncSession> getSessionHistory(Pageable pageable);

    void processErrorRecord(Long recordId, String processedBy, String note);
}
