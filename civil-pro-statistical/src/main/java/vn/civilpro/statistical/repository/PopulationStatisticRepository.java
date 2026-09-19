package vn.civilpro.statistical.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.statistical.entity.PopulationStatistic;

import java.util.List;
import java.util.Optional;

@Repository
public interface PopulationStatisticRepository extends JpaRepository<PopulationStatistic, Long> {

    Optional<PopulationStatistic> findByAreaCodeAndYearAndMonth(String areaCode, Integer year, Integer month);

    List<PopulationStatistic> findByYearAndMonth(Integer year, Integer month);

    Optional<PopulationStatistic> findTopByAreaCodeOrderByCalculatedAtDesc(String areaCode);

    Page<PopulationStatistic> findByYearOrderByAreaCodeAsc(Integer year, Pageable pageable);
}
