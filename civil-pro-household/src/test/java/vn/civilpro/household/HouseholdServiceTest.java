package vn.civilpro.household;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import vn.civil.grpc.citizen.CitizenGrpcServiceGrpc;
import vn.civil.grpc.citizen.CitizenInfo;
import vn.civil.grpc.citizen.GetCitizenByIdRequest;
import vn.civil.grpc.citizen.GetCitizenResponse;
import vn.civil.grpc.common.GrpcResponse;
import vn.civilpro.household.model.dto.request.AddMemberRequest;
import vn.civilpro.household.model.dto.request.CreateHouseholdRequest;
import vn.civilpro.household.model.dto.response.HouseholdDetailResponse;
import vn.civilpro.household.model.entity.Household;
import vn.civilpro.household.model.entity.HouseholdMember;
import vn.civilpro.household.event.HouseholdEventPublisher;
import vn.civilpro.household.exception.BusinessException;
import vn.civilpro.household.mapper.HouseholdMapper;
import vn.civilpro.household.repository.HouseholdMemberRepository;
import vn.civilpro.household.repository.HouseholdRepository;
import vn.civilpro.household.service.impl.HouseholdServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HouseholdServiceTest {

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private HouseholdMemberRepository memberRepository;

    @Mock
    private HouseholdMapper householdMapper;

    @Mock
    private HouseholdEventPublisher eventPublisher;

    @Mock
    private CitizenGrpcServiceGrpc.CitizenGrpcServiceBlockingStub citizenGrpcStub;

    @InjectMocks
    private HouseholdServiceImpl householdService;

    private Household sampleHousehold;

    @BeforeEach
    void setUp() {
        householdService.setCitizenGrpcStub(citizenGrpcStub);

        sampleHousehold = Household.builder()
                .id(1L)
                .householdCode("HH-2026-TEST001")
                .headCitizenId(10L)
                .headFullName("Nguyen Van A")
                .areaCode("01001")
                .fullAddress("123 Test Street")
                .memberCount(1)
                .status("ACTIVE")
                .registrationDate(LocalDate.now())
                .build();
    }

    @Test
    void testCreateHousehold_Success() {
        CreateHouseholdRequest request = CreateHouseholdRequest.builder()
                .headCitizenId(10L)
                .areaCode("01001")
                .fullAddress("123 Test Street")
                .build();

        CitizenInfo citizenInfo = CitizenInfo.newBuilder()
                .setId(10L)
                .setFullName("Nguyen Van A")
                .setStatus(1)
                .build();

        GetCitizenResponse grpcResponse = GetCitizenResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setData(citizenInfo)
                .build();

        when(citizenGrpcStub.getById(any(GetCitizenByIdRequest.class))).thenReturn(grpcResponse);
        when(householdRepository.save(any(Household.class))).thenReturn(sampleHousehold);
        when(householdMapper.toDetailResponse(any(Household.class))).thenReturn(
                HouseholdDetailResponse.builder().id(1L).householdCode("HH-2026-TEST001").headFullName("Nguyen Van A").build()
        );

        HouseholdDetailResponse result = householdService.create(request);

        assertNotNull(result);
        assertEquals("HH-2026-TEST001", result.getHouseholdCode());
        verify(eventPublisher, times(1)).publishHouseholdCreated(any());
    }

    @Test
    void testCreateHousehold_CitizenDeceased_ThrowsException() {
        CreateHouseholdRequest request = CreateHouseholdRequest.builder()
                .headCitizenId(10L)
                .areaCode("01001")
                .fullAddress("123 Test Street")
                .build();

        CitizenInfo citizenInfo = CitizenInfo.newBuilder()
                .setId(10L)
                .setFullName("Nguyen Van A")
                .setStatus(0) // Deceased
                .build();

        GetCitizenResponse grpcResponse = GetCitizenResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setData(citizenInfo)
                .build();

        when(citizenGrpcStub.getById(any(GetCitizenByIdRequest.class))).thenReturn(grpcResponse);

        assertThrows(BusinessException.class, () -> householdService.create(request));
    }

    @Test
    void testAddMember_Success() {
        when(householdRepository.findById(1L)).thenReturn(Optional.of(sampleHousehold));
        when(memberRepository.existsByHouseholdIdAndCitizenIdAndStatus(1L, 20L, 1)).thenReturn(false);

        CitizenInfo citizenInfo = CitizenInfo.newBuilder()
                .setId(20L)
                .setFullName("Nguyen Van B")
                .setStatus(1)
                .build();

        GetCitizenResponse grpcResponse = GetCitizenResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setData(citizenInfo)
                .build();

        when(citizenGrpcStub.getById(any(GetCitizenByIdRequest.class))).thenReturn(grpcResponse);

        AddMemberRequest request = AddMemberRequest.builder()
                .citizenId(20L)
                .relationshipWithHead("SPOUSE")
                .build();

        assertDoesNotThrow(() -> householdService.addMember(1L, request));
        verify(memberRepository, times(1)).save(any(HouseholdMember.class));
        verify(eventPublisher, times(1)).publishMemberAdded(any());
    }
}
