package vn.civilpro.sync.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.sync.entity.SyncSession;
import vn.civilpro.sync.service.SyncSessionService;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncSessionController {

    private final SyncSessionService syncService;

    @PostMapping("/trigger")
    public ResponseEntity<SyncSession> trigger(@RequestParam(defaultValue = "INCREMENTAL") String syncType) {
        return ResponseEntity.ok(syncService.triggerSync(syncType));
    }

    @GetMapping("/latest")
    public ResponseEntity<SyncSession> getLatest() {
        return ResponseEntity.of(syncService.getLatestSession());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<SyncSession>> getHistory(Pageable pageable) {
        return ResponseEntity.ok(syncService.getSessionHistory(pageable));
    }

    @PostMapping("/errors/{id}/resolve")
    public ResponseEntity<Void> resolveError(
            @PathVariable Long id,
            @RequestParam String processedBy,
            @RequestParam(required = false) String note) {
        syncService.processErrorRecord(id, processedBy, note);
        return ResponseEntity.ok().build();
    }
}
