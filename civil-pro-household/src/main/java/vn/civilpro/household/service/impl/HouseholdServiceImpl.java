package vn.civilpro.household.service.impl;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civil.grpc.citizen.CitizenGrpcServiceGrpc;
import vn.civil.grpc.citizen.CitizenInfo;
import vn.civil.grpc.citizen.GetCitizenByIdRequest;
import vn.civil.grpc.citizen.GetCitizenResponse;
import vn.civilpro.household.model.dto.request.AddMemberRequest;
import vn.civilpro.household.model.dto.request.CreateHouseholdRequest;
import vn.civilpro.household.model.dto.request.UpdateHouseholdRequest;
import vn.civilpro.household.model.dto.response.HouseholdDetailResponse;
import vn.civilpro.household.model.dto.response.HouseholdMemberResponse;
import vn.civilpro.household.model.entity.Household;
import vn.civilpro.household.model.entity.HouseholdMember;
import vn.civilpro.household.event.HouseholdEvent;
import vn.civilpro.household.event.HouseholdEventPublisher;
import vn.civilpro.household.exception.BusinessException;
import vn.civilpro.household.exception.ResourceNotFoundException;
import vn.civilpro.household.mapper.HouseholdMapper;
import vn.civilpro.household.repository.HouseholdMemberRepository;
import vn.civilpro.household.repository.HouseholdRepository;
import vn.civilpro.household.service.HouseholdService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseholdServiceImpl implements HouseholdService {

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository memberRepository;
    private final HouseholdMapper householdMapper;
    private final HouseholdEventPublisher eventPublisher;

    @GrpcClient("citizen-service")
    private CitizenGrpcServiceGrpc.CitizenGrpcServiceBlockingStub citizenGrpcStub;

    public void setCitizenGrpcStub(CitizenGrpcServiceGrpc.CitizenGrpcServiceBlockingStub citizenGrpcStub) {
        this.citizenGrpcStub = citizenGrpcStub;
    }

    @Override
    @Transactional
    public HouseholdDetailResponse create(CreateHouseholdRequest request) {
        log.info("[HouseholdService] Creating new household for headCitizenId: {}", request.getHeadCitizenId());

        // Validate head citizen via gRPC
        CitizenInfo citizenInfo = getCitizenViaGrpc(request.getHeadCitizenId());
        if (citizenInfo.getStatus() != 1) {
            throw new BusinessException(400, "Citizen is not active or deceased (status=" + citizenInfo.getStatus() + ")");
        }

        String code = generateHouseholdCode();
        Household household = Household.builder()
                .householdCode(code)
                .householdBookNumber(request.getHouseholdBookNumber() != null ? request.getHouseholdBookNumber() : code)
                .headCitizenId(request.getHeadCitizenId())
                .headFullName(citizenInfo.getFullName())
                .areaCode(request.getAreaCode())
                .fullAddress(request.getFullAddress())
                .memberCount(1)
                .householdType(request.getHouseholdType() != null ? request.getHouseholdType() : "NORMAL")
                .registrationDate(LocalDate.now())
                .status("ACTIVE")
                .notes(request.getNotes())
                .build();

        Household saved = householdRepository.save(household);

        HouseholdMember headMember = HouseholdMember.builder()
                .household(saved)
                .citizenId(request.getHeadCitizenId())
                .fullName(citizenInfo.getFullName())
                .relationshipWithHead("HEAD")
                .joinDate(LocalDate.now())
                .status(1)
                .build();

        memberRepository.save(headMember);
        saved.setMembers(List.of(headMember));

        // Publish event to Kafka
        eventPublisher.publishHouseholdCreated(HouseholdEvent.builder()
                .eventType("HOUSEHOLD_CREATED")
                .householdId(saved.getId())
                .householdCode(saved.getHouseholdCode())
                .citizenId(saved.getHeadCitizenId())
                .citizenFullName(saved.getHeadFullName())
                .areaCode(saved.getAreaCode())
                .relationshipWithHead("HEAD")
                .build());

        return householdMapper.toDetailResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "household", key = "#id"),
        @CacheEvict(value = "householdCodeLookup", allEntries = true)
    })
    public HouseholdDetailResponse update(Long id, UpdateHouseholdRequest request) {
        Household household = findByIdOrThrow(id);

        if (request.getHouseholdBookNumber() != null) {
            household.setHouseholdBookNumber(request.getHouseholdBookNumber());
        }
        if (request.getFullAddress() != null) {
            household.setFullAddress(request.getFullAddress());
        }
        if (request.getHouseholdType() != null) {
            household.setHouseholdType(request.getHouseholdType());
        }
        if (request.getStatus() != null) {
            household.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            household.setNotes(request.getNotes());
        }

        Household updated = householdRepository.save(household);
        return householdMapper.toDetailResponse(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "household", key = "#householdId"),
        @CacheEvict(value = "householdMembers", allEntries = true)
    })
    public void addMember(Long householdId, AddMemberRequest request) {
        Household household = findByIdOrThrow(householdId);

        boolean alreadyInHousehold = memberRepository.existsByHouseholdIdAndCitizenIdAndStatus(
                householdId, request.getCitizenId(), 1);
        if (alreadyInHousehold) {
            throw new BusinessException(400, "Citizen is already an active member of this household");
        }

        CitizenInfo citizenInfo = getCitizenViaGrpc(request.getCitizenId());
        if (citizenInfo.getStatus() != 1) {
            throw new BusinessException(400, "Citizen is not active or deceased");
        }

        HouseholdMember member = HouseholdMember.builder()
                .household(household)
                .citizenId(request.getCitizenId())
                .fullName(citizenInfo.getFullName())
                .relationshipWithHead(request.getRelationshipWithHead())
                .joinDate(LocalDate.now())
                .status(1)
                .build();

        memberRepository.save(member);

        household.setMemberCount(household.getMemberCount() + 1);
        householdRepository.save(household);

        eventPublisher.publishMemberAdded(HouseholdEvent.builder()
                .eventType("HOUSEHOLD_MEMBER_ADDED")
                .householdId(household.getId())
                .householdCode(household.getHouseholdCode())
                .citizenId(request.getCitizenId())
                .citizenFullName(citizenInfo.getFullName())
                .areaCode(household.getAreaCode())
                .relationshipWithHead(request.getRelationshipWithHead())
                .build());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "household", key = "#householdId"),
        @CacheEvict(value = "householdMembers", allEntries = true)
    })
    public void removeMember(Long householdId, Long citizenId, String reason) {
        HouseholdMember member = memberRepository.findByHouseholdIdAndCitizenIdAndStatus(householdId, citizenId, 1)
                .orElseThrow(() -> new ResourceNotFoundException("HouseholdMember", citizenId));

        member.setStatus(0);
        member.setLeaveDate(LocalDate.now());
        member.setLeaveReason(reason);
        memberRepository.save(member);

        Household household = findByIdOrThrow(householdId);
        household.setMemberCount(Math.max(0, household.getMemberCount() - 1));
        householdRepository.save(household);
    }

    @Override
    @Cacheable(value = "household", key = "#id", unless = "#result == null")
    public HouseholdDetailResponse getById(Long id) {
        Household household = findByIdOrThrow(id);
        return householdMapper.toDetailResponse(household);
    }

    @Override
    @Cacheable(value = "householdCodeLookup", key = "#householdCode", unless = "#result == null")
    public HouseholdDetailResponse getByHouseholdCode(String householdCode) {
        Household household = householdRepository.findByHouseholdCode(householdCode)
                .orElseThrow(() -> new ResourceNotFoundException("Household", householdCode));
        return householdMapper.toDetailResponse(household);
    }

    @Override
    public HouseholdDetailResponse getByCitizenId(Long citizenId) {
        Household household = householdRepository.findHouseholdByCitizenId(citizenId)
                .orElseThrow(() -> new ResourceNotFoundException("Household for citizen", citizenId));
        return householdMapper.toDetailResponse(household);
    }

    @Override
    @Cacheable(value = "householdMembers", key = "#householdId + '-' + #activeOnly", unless = "#result == null")
    public List<HouseholdMemberResponse> getMembers(Long householdId, boolean activeOnly) {
        List<HouseholdMember> list = activeOnly
                ? memberRepository.findByHouseholdIdAndStatus(householdId, 1)
                : memberRepository.findByHouseholdId(householdId);
        return householdMapper.toMemberResponseList(list);
    }

    @Override
    public long countHouseholdsByArea(String areaCode) {
        return householdRepository.countByAreaCode(areaCode);
    }

    @Override
    public Page<HouseholdDetailResponse> search(String areaCode, String headFullName, String status, String householdType, Pageable pageable) {
        return householdRepository.search(areaCode, headFullName, status, householdType, pageable)
                .map(householdMapper::toDetailResponse);
    }

    // ---- gRPC helper ----

    public CitizenInfo getCitizenViaGrpc(Long citizenId) {
        try {
            if (citizenGrpcStub == null) {
                log.warn("[gRPC] citizenGrpcStub is null, fallback for test or unconfigured stub");
                return CitizenInfo.newBuilder()
                        .setId(citizenId)
                        .setFullName("Unknown Citizen")
                        .setStatus(1)
                        .build();
            }

            GetCitizenResponse response = citizenGrpcStub.getById(
                    GetCitizenByIdRequest.newBuilder().setId(citizenId).build()
            );

            if (!response.getMeta().getSuccess()) {
                throw new ResourceNotFoundException("Citizen", citizenId);
            }

            return response.getData();
        } catch (StatusRuntimeException e) {
            log.error("[gRPC] Error calling Citizen Service: {}", e.getStatus());
            throw new BusinessException(503, "Citizen service unavailable: " + e.getStatus().getDescription());
        }
    }

    private Household findByIdOrThrow(Long id) {
        return householdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Household", id));
    }

    private String generateHouseholdCode() {
        return "HH-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
