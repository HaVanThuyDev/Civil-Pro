package vn.civilpro.fluctuations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;
import vn.civilpro.fluctuations.service.PopulationFluctuationService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fluctuations")
@RequiredArgsConstructor
public class PopulationFluctuationController {

    private final PopulationFluctuationService fluctuationService;

    @PostMapping
    public ResponseEntity<PopulationFluctuation> record(@RequestBody PopulationFluctuation fluctuation) {
        return ResponseEntity.ok(fluctuationService.recordFluctuation(fluctuation));
    }

    @GetMapping
    public ResponseEntity<Page<PopulationFluctuation>> search(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Pageable pageable) {

        LocalDate from = fromDate != null && !fromDate.isBlank() ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null && !toDate.isBlank() ? LocalDate.parse(toDate) : null;

        return ResponseEntity.ok(fluctuationService.search(areaCode, type, from, to, pageable));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<Object[]>> getMonthly(
            @RequestParam(required = false) String areaCode,
            @RequestParam(defaultValue = "2026") int year,
            @RequestParam(defaultValue = "1") int fromMonth,
            @RequestParam(defaultValue = "12") int toMonth) {
        return ResponseEntity.ok(fluctuationService.getMonthlySummary(areaCode, year, fromMonth, toMonth));
    }

    @GetMapping("/annual")
    public ResponseEntity<List<Object[]>> getAnnual(
            @RequestParam(required = false) String areaCode,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(fluctuationService.getAnnualBirthDeath(areaCode, year));
    }
}
