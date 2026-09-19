package vn.civilpro.household.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.civilpro.household.model.entity.Household;

import java.util.Optional;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {

    Optional<Household> findByHouseholdCode(String householdCode);

    boolean existsByHouseholdCode(String householdCode);

    Page<Household> findByAreaCodeAndStatus(String areaCode, String status, Pageable pageable);

    @Query("""
        SELECT h FROM Household h
        JOIN h.members m
        WHERE m.citizenId = :citizenId
        AND m.status = 1
        AND h.status = 'ACTIVE'
        """)
    Optional<Household> findHouseholdByCitizenId(@Param("citizenId") Long citizenId);

    @Query("SELECT COUNT(h) FROM Household h WHERE h.areaCode = :areaCode AND h.status = 'ACTIVE'")
    long countByAreaCode(@Param("areaCode") String areaCode);

    @Query("""
        SELECT h FROM Household h
        WHERE (:areaCode IS NULL OR h.areaCode = :areaCode)
        AND (:headFullName IS NULL OR LOWER(h.headFullName) LIKE LOWER(CONCAT('%', :headFullName, '%')))
        AND (:status IS NULL OR h.status = :status)
        AND (:householdType IS NULL OR h.householdType = :householdType)
        """)
    Page<Household> search(
            @Param("areaCode") String areaCode,
            @Param("headFullName") String headFullName,
            @Param("status") String status,
            @Param("householdType") String householdType,
            Pageable pageable
    );
}
