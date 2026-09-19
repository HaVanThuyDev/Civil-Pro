package vn.civilpro.statistical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import vn.civil.grpc.citizen.*;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.fluctuation.FluctuationGrpcServiceGrpc;
import vn.civil.grpc.fluctuation.GetMonthlyFluctuationsRequest;
import vn.civil.grpc.fluctuation.GetMonthlyFluctuationsResponse;
import vn.civil.grpc.fluctuation.MonthlyFluctuationSummary;
import vn.civil.grpc.household.CountHouseholdsByAreaRequest;
import vn.civil.grpc.household.CountHouseholdsResponse;
import vn.civil.grpc.household.HouseholdGrpcServiceGrpc;
import vn.civil.grpc.statistical.GetDashboardResponse;
import vn.civilpro.statistical.repository.PopulationStatisticRepository;
import vn.civilpro.statistical.service.impl.StatisticalServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StatisticalServiceTest {

    @Mock
    private PopulationStatisticRepository statisticRepository;

    @Mock
    private CitizenGrpcServiceGrpc.CitizenGrpcServiceBlockingStub citizenGrpcStub;

    @Mock
    private HouseholdGrpcServiceGrpc.HouseholdGrpcServiceBlockingStub householdGrpcStub;

    @Mock
    private FluctuationGrpcServiceGrpc.FluctuationGrpcServiceBlockingStub fluctuationGrpcStub;

    @InjectMocks
    private StatisticalServiceImpl statisticalService;

    @BeforeEach
    void setUp() {
        statisticalService.setCitizenGrpcStub(citizenGrpcStub);
        statisticalService.setHouseholdGrpcStub(householdGrpcStub);
        statisticalService.setFluctuationGrpcStub(fluctuationGrpcStub);
    }

    @Test
    void testGetDashboard_AggregatesFromAllThreeServices() {
        // 1. Citizen stub responses
        CountResponse popCount = CountResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setCount(1500000L)
                .build();
        when(citizenGrpcStub.countPopulationByAddress(any(CountPopulationByAddressRequest.class))).thenReturn(popCount);

        GetCitizenListResponse expIds = GetCitizenListResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setTotal(2841)
                .build();
        when(citizenGrpcStub.getExpiringNationalIds(any(GetExpiringNationalIdRequest.class))).thenReturn(expIds);

        AgeStructure ageStruct = AgeStructure.newBuilder()
                .setPopulation014(300000)
                .setPopulation1564(1000000)
                .setPopulation65Plus(200000)
                .setTotalPopulation(1500000)
                .build();
        GetAgeStructureResponse ageResp = GetAgeStructureResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setData(ageStruct)
                .build();
        when(citizenGrpcStub.getAgeStructure(any(GetAgeStructureRequest.class))).thenReturn(ageResp);

        // 2. Household stub response
        CountHouseholdsResponse hhCount = CountHouseholdsResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .setCount(450000L)
                .build();
        when(householdGrpcStub.countHouseholdsByArea(any(CountHouseholdsByAreaRequest.class))).thenReturn(hhCount);

        // 3. Fluctuation stub response
        GetMonthlyFluctuationsResponse flucResp = GetMonthlyFluctuationsResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).build())
                .addData(MonthlyFluctuationSummary.newBuilder().setMonth(1).setBirthCount(120).setDeathCount(45).build())
                .addData(MonthlyFluctuationSummary.newBuilder().setMonth(2).setBirthCount(110).setDeathCount(40).build())
                .build();
        when(fluctuationGrpcStub.getMonthlyFluctuations(any(GetMonthlyFluctuationsRequest.class))).thenReturn(flucResp);

        GetDashboardResponse dashboard = statisticalService.getDashboard("01");

        assertNotNull(dashboard);
        assertTrue(dashboard.getMeta().getSuccess());
        assertEquals(1500000L, dashboard.getSummary().getTotalPopulation());
        assertEquals(450000L, dashboard.getSummary().getTotalHouseholds());
        assertEquals(2841L, dashboard.getSummary().getExpiringNationalIds());
        assertEquals(2, dashboard.getFluctuationChart().getBirthsCount());
        assertEquals(2, dashboard.getFluctuationChart().getDeathsCount());
        assertEquals(20.0, dashboard.getAgeStructure().getPct014());
    }
}
