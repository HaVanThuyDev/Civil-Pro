package vn.civilpro.fluctuations.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;

import java.time.LocalDate;
import java.util.List;

public interface PopulationFluctuationService {

    PopulationFluctuation recordFluctuation(PopulationFluctuation fluctuation);

    Page<PopulationFluctuation> search(String areaCode, String type, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    List<Object[]> getMonthlySummary(String areaCode, int year, int fromMonth, int toMonth);

    List<Object[]> getAnnualBirthDeath(String areaCode, int year);
}
