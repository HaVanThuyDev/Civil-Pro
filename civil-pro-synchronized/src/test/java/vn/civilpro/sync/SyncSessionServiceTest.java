package vn.civilpro.sync;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.civilpro.sync.entity.SyncSession;
import vn.civilpro.sync.repository.SyncErrorRecordRepository;
import vn.civilpro.sync.repository.SyncSessionRepository;
import vn.civilpro.sync.service.impl.SyncSessionServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncSessionServiceTest {

    @Mock
    private SyncSessionRepository sessionRepository;

    @Mock
    private SyncErrorRecordRepository errorRecordRepository;

    @InjectMocks
    private SyncSessionServiceImpl syncService;

    @Test
    void testTriggerSync() {
        SyncSession mockSession = SyncSession.builder()
                .id(1L)
                .sessionCode("SYNC-TEST01")
                .syncType("FULL")
                .startTime(LocalDateTime.now())
                .status("RUNNING")
                .completionPercentage(80.0)
                .build();

        when(sessionRepository.save(any(SyncSession.class))).thenReturn(mockSession);

        SyncSession result = syncService.triggerSync("FULL");
        assertNotNull(result);
        assertEquals("SYNC-TEST01", result.getSessionCode());
        assertEquals(80.0, result.getCompletionPercentage());
        verify(sessionRepository, times(1)).save(any(SyncSession.class));
    }

    @Test
    void testGetLatestSession() {
        SyncSession mockSession = SyncSession.builder()
                .id(1L)
                .sessionCode("SYNC-TEST01")
                .startTime(LocalDateTime.now())
                .build();

        when(sessionRepository.findTopByOrderByStartTimeDesc()).thenReturn(Optional.of(mockSession));

        Optional<SyncSession> result = syncService.getLatestSession();
        assertTrue(result.isPresent());
        assertEquals("SYNC-TEST01", result.get().getSessionCode());
    }
}
