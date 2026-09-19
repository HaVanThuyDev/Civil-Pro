package vn.civilpro.fluctuations.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PopulationFluctuationRepository extends JpaRepository<PopulationFluctuation, Long> {

    @Query("""
        SELECT f FROM PopulationFluctuation f
        WHERE (:areaCode IS NULL OR :areaCode = '' OR f.areaCode = :areaCode)
        AND (:fluctuationType IS NULL OR :fluctuationType = '' OR f.fluctuationType = :fluctuationType)
        AND (:fromDate IS NULL OR f.fluctuationDate >= :fromDate)
        AND (:toDate IS NULL OR f.fluctuationDate <= :toDate)
        """)
    Page<PopulationFluctuation> search(
            @Param("areaCode") String areaCode,
            @Param("fluctuationType") String fluctuationType,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query("""
        SELECT f.month, f.fluctuationType, COUNT(f)
        FROM PopulationFluctuation f
        WHERE (:areaCode IS NULL OR :areaCode = '' OR f.areaCode = :areaCode)
        AND f.year = :year
        AND f.month BETWEEN :fromMonth AND :toMonth
        GROUP BY f.month, f.fluctuationType
        ORDER BY f.month ASC
        """)
    List<Object[]> aggregateMonthlySummary(
            @Param("areaCode") String areaCode,
            @Param("year") int year,
            @Param("fromMonth") int fromMonth,
            @Param("toMonth") int toMonth
    );

    @Query("""
        SELECT f.fluctuationType, COUNT(f)
        FROM PopulationFluctuation f
        WHERE (:areaCode IS NULL OR :areaCode = '' OR f.areaCode = :areaCode)
        AND f.year = :year
        AND f.fluctuationType IN ('BIRTH', 'DEATH')
        GROUP BY f.fluctuationType
        """)
    List<Object[]> countAnnualBirthDeath(
            @Param("areaCode") String areaCode,
            @Param("year") int year
    );
}
