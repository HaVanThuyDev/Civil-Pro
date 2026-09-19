package vn.civilpro.household.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.civilpro.household.model.entity.HouseholdMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {

    List<HouseholdMember> findByHouseholdId(Long householdId);

    List<HouseholdMember> findByHouseholdIdAndStatus(Long householdId, Integer status);

    boolean existsByHouseholdIdAndCitizenIdAndStatus(Long householdId, Long citizenId, Integer status);

    Optional<HouseholdMember> findByHouseholdIdAndCitizenIdAndStatus(Long householdId, Long citizenId, Integer status);
}
