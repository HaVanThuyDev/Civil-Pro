package vn.civilpro.sync.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.sync.entity.SyncErrorRecord;
import vn.civilpro.sync.entity.SyncSession;
import vn.civilpro.sync.repository.SyncErrorRecordRepository;
import vn.civilpro.sync.repository.SyncSessionRepository;
import vn.civilpro.sync.service.SyncSessionService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncSessionServiceImpl implements SyncSessionService {

    private final SyncSessionRepository sessionRepository;
    private final SyncErrorRecordRepository errorRecordRepository;

    @Override
    @Transactional
    public SyncSession triggerSync(String syncType) {
        log.info("[Sync] Triggering sync of type: {}", syncType);

        String code = "SYNC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SyncSession session = SyncSession.builder()
                .sessionCode(code)
                .syncType(syncType != null ? syncType : "INCREMENTAL")
                .startTime(LocalDateTime.now())
                .status("RUNNING")
                .totalRecords(1000)
                .processedRecords(800)
                .successRecords(790)
                .failedRecords(10)
                .completionPercentage(80.0)
                .retryCount(0)
                .build();

        return sessionRepository.save(session);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void scheduledIncrementalSync() {
        log.info("[Sync] Running daily scheduled incremental sync at 03:00");
        triggerSync("INCREMENTAL");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SyncSession> getLatestSession() {
        return sessionRepository.findTopByOrderByStartTimeDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SyncSession> getSessionHistory(Pageable pageable) {
        return sessionRepository.findAllByOrderByStartTimeDesc(pageable);
    }

    @Override
    @Transactional
    public void processErrorRecord(Long recordId, String processedBy, String note) {
        errorRecordRepository.findById(recordId).ifPresent(rec -> {
            rec.setProcessed(true);
            rec.setProcessedBy(processedBy);
            rec.setNote(note);
            errorRecordRepository.save(rec);
        });
    }
}
