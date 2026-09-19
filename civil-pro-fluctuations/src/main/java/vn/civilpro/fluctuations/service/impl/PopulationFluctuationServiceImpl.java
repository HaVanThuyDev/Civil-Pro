package vn.civilpro.fluctuations.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;
import vn.civilpro.fluctuations.repository.PopulationFluctuationRepository;
import vn.civilpro.fluctuations.service.PopulationFluctuationService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopulationFluctuationServiceImpl implements PopulationFluctuationService {

    private final PopulationFluctuationRepository fluctuationRepository;

    @Override
    @Transactional
    public PopulationFluctuation recordFluctuation(PopulationFluctuation fluctuation) {
        log.info("[FluctuationService] Recording fluctuation: type={}, area={}",
                fluctuation.getFluctuationType(), fluctuation.getAreaCode());
        return fluctuationRepository.save(fluctuation);
    }

    @Override
    public Page<PopulationFluctuation> search(String areaCode, String type, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return fluctuationRepository.search(areaCode, type, fromDate, toDate, pageable);
    }

    @Override
    public List<Object[]> getMonthlySummary(String areaCode, int year, int fromMonth, int toMonth) {
        return fluctuationRepository.aggregateMonthlySummary(areaCode, year, fromMonth, toMonth);
    }

    @Override
    public List<Object[]> getAnnualBirthDeath(String areaCode, int year) {
        return fluctuationRepository.countAnnualBirthDeath(areaCode, year);
    }
}
