package vn.civilpro.congdan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.civilpro.congdan.entity.Citizen;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long>, JpaSpecificationExecutor<Citizen> {

    Optional<Citizen> findByIdCardNumber(String idCardNumber);

    Optional<Citizen> findByCitizenCode(String citizenCode);

    boolean existsByIdCardNumber(String idCardNumber);

    boolean existsByIdCardNumberAndIdNot(String idCardNumber, Long excludeId);

    List<Citizen> findByIdIn(List<Long> ids);

    Page<Citizen> findByPermanentAreaCodeAndStatus(String permanentAreaCode, Integer status, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Citizen c WHERE c.permanentAreaCode = :areaCode AND c.status = 1")
    long countPopulationByAreaCode(@Param("areaCode") String areaCode);

    @Query("""
        SELECT c FROM Citizen c
        WHERE (:fullName IS NULL OR LOWER(c.fullNameAscii) LIKE LOWER(CONCAT('%', :fullName, '%')))
        AND (:areaCode IS NULL OR c.permanentAreaCode = :areaCode)
        AND (:citizenType IS NULL OR c.citizenType = :citizenType)
        AND (:status IS NULL OR c.status = :status)
        """)
    Page<Citizen> searchCitizen(
            @Param("fullName") String fullName,
            @Param("areaCode") String areaCode,
            @Param("citizenType") String citizenType,
            @Param("status") Integer status,
            Pageable pageable
    );

    @Query("""
        SELECT
            SUM(CASE WHEN YEAR(CURRENT_DATE) - YEAR(c.dateOfBirth) <= 14 THEN 1 ELSE 0 END) AS age0To14,
            SUM(CASE WHEN YEAR(CURRENT_DATE) - YEAR(c.dateOfBirth) BETWEEN 15 AND 64 THEN 1 ELSE 0 END) AS age15To64,
            SUM(CASE WHEN YEAR(CURRENT_DATE) - YEAR(c.dateOfBirth) >= 65 THEN 1 ELSE 0 END) AS age65Plus
        FROM Citizen c
        WHERE c.permanentAreaCode LIKE CONCAT(:areaCodePrefix, '%')
        AND c.status = 1
        """)
    Object[] getAgeStructure(@Param("areaCodePrefix") String areaCodePrefix);

    @Query("""
        SELECT c FROM Citizen c
        WHERE c.idCardExpiryDate IS NOT NULL
        AND c.idCardExpiryDate BETWEEN :fromDate AND :toDate
        AND c.status = 1
        """)
    List<Citizen> findExpiringIdCards(@Param("fromDate") LocalDate fromDate,
                                      @Param("toDate") LocalDate toDate);

    @Modifying
    @Query("UPDATE Citizen c SET c.householdId = :householdId WHERE c.id IN :ids")
    int updateHouseholdForCitizenList(@Param("householdId") Long householdId,
                                      @Param("ids") List<Long> ids);
}