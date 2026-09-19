package vn.civilpro.statistical.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.civilpro.statistical.service.StatisticalService;

import java.util.Map;

@RestController
@RequestMapping("/api/statistical")
@RequiredArgsConstructor
public class StatisticalController {

    private final StatisticalService statisticalService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@RequestParam(required = false) String areaCode) {
        var resp = statisticalService.getDashboard(areaCode);
        return ResponseEntity.ok(Map.of(
                "totalPopulation", resp.getSummary().getTotalPopulation(),
                "totalHouseholds", resp.getSummary().getTotalHouseholds(),
                "temporaryResidents", resp.getSummary().getTemporaryResidents(),
                "expiringNationalIds", resp.getSummary().getExpiringNationalIds(),
                "growthRatePct", resp.getSummary().getGrowthRatePct(),
                "updatedAt", resp.getSummary().getUpdatedAt()
        ));
    }
}
