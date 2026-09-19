package vn.civilpro.statistical.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.civil.grpc.citizen.*;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.common.PageInfo;
import vn.civil.grpc.fluctuation.FluctuationGrpcServiceGrpc;
import vn.civil.grpc.fluctuation.GetMonthlyFluctuationsRequest;
import vn.civil.grpc.fluctuation.GetMonthlyFluctuationsResponse;
import vn.civil.grpc.household.CountHouseholdsByAreaRequest;
import vn.civil.grpc.household.CountHouseholdsResponse;
import vn.civil.grpc.household.HouseholdGrpcServiceGrpc;
import vn.civil.grpc.statistical.*;
import vn.civilpro.statistical.entity.PopulationStatistic;
import vn.civilpro.statistical.repository.PopulationStatisticRepository;
import vn.civilpro.statistical.service.StatisticalService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticalServiceImpl implements StatisticalService {

    private final PopulationStatisticRepository statisticRepository;

    @GrpcClient("citizen-service")
    private CitizenGrpcServiceGrpc.CitizenGrpcServiceBlockingStub citizenGrpcStub;

    @GrpcClient("household-service")
    private HouseholdGrpcServiceGrpc.HouseholdGrpcServiceBlockingStub householdGrpcStub;

    @GrpcClient("fluctuation-service")
    private FluctuationGrpcServiceGrpc.FluctuationGrpcServiceBlockingStub fluctuationGrpcStub;

    // Setters for unit testing
    public void setCitizenGrpcStub(CitizenGrpcServiceGrpc.CitizenGrpcServiceBlockingStub stub) {
        this.citizenGrpcStub = stub;
    }

    public void setHouseholdGrpcStub(HouseholdGrpcServiceGrpc.HouseholdGrpcServiceBlockingStub stub) {
        this.householdGrpcStub = stub;
    }

    public void setFluctuationGrpcStub(FluctuationGrpcServiceGrpc.FluctuationGrpcServiceBlockingStub stub) {
        this.fluctuationGrpcStub = stub;
    }

    @Override
    public GetDashboardResponse getDashboard(String areaCode) {
        log.info("[StatisticalService] Aggregating dashboard data for area: {}", areaCode);

        // 1. Query Citizen Service for total population & expiring IDs & age structure
        long totalPopulation = 0L;
        long expiringIds = 0L;
        AgeStructure ageStructureData = null;

        if (citizenGrpcStub != null) {
            try {
                CountResponse popResp = citizenGrpcStub.countPopulationByAddress(
                        CountPopulationByAddressRequest.newBuilder().setAddressCode(areaCode != null ? areaCode : "").build()
                );
                if (popResp.getMeta().getSuccess()) {
                    totalPopulation = popResp.getCount();
                }
            } catch (Exception e) {
                log.warn("[StatisticalService] Failed to query citizen count: {}", e.getMessage());
            }

            try {
                GetCitizenListResponse expResp = citizenGrpcStub.getExpiringNationalIds(
                        GetExpiringNationalIdRequest.newBuilder()
                                .setDaysThreshold(60)
                                .setAddressCode(areaCode != null ? areaCode : "")
                                .build()
                );
                if (expResp.getMeta().getSuccess()) {
                    expiringIds = expResp.getTotal();
                }
            } catch (Exception e) {
                log.warn("[StatisticalService] Failed to query expiring CCCDs: {}", e.getMessage());
            }

            try {
                GetAgeStructureResponse ageResp = citizenGrpcStub.getAgeStructure(
                        GetAgeStructureRequest.newBuilder().setAddressCodePrefix(areaCode != null ? areaCode : "").build()
                );
                if (ageResp.getMeta().getSuccess()) {
                    ageStructureData = ageResp.getData();
                }
            } catch (Exception e) {
                log.warn("[StatisticalService] Failed to query age structure: {}", e.getMessage());
            }
        }

        // 2. Query Household Service for household count
        long totalHouseholds = 0L;
        if (householdGrpcStub != null) {
            try {
                CountHouseholdsResponse hhResp = householdGrpcStub.countHouseholdsByArea(
                        CountHouseholdsByAreaRequest.newBuilder().setAreaCode(areaCode != null ? areaCode : "").build()
                );
                if (hhResp.getMeta().getSuccess()) {
                    totalHouseholds = hhResp.getCount();
                }
            } catch (Exception e) {
                log.warn("[StatisticalService] Failed to query household count: {}", e.getMessage());
            }
        }

        // 3. Query Fluctuation Service for 12-month fluctuation trend chart
        FluctuationSeries.Builder flucChartBuilder = FluctuationSeries.newBuilder();
        if (fluctuationGrpcStub != null) {
            try {
                GetMonthlyFluctuationsResponse flucResp = fluctuationGrpcStub.getMonthlyFluctuations(
                        GetMonthlyFluctuationsRequest.newBuilder()
                                .setAreaCode(areaCode != null ? areaCode : "")
                                .setYear(LocalDate.now().getYear())
                                .setFromMonth(1)
                                .setToMonth(12)
                                .build()
                );
                if (flucResp.getMeta().getSuccess()) {
                    for (var item : flucResp.getDataList()) {
                        String label = "T" + item.getMonth();
                        flucChartBuilder.addBirths(DataPoint.newBuilder().setLabel(label).setValue(item.getBirthCount()).build());
                        flucChartBuilder.addDeaths(DataPoint.newBuilder().setLabel(label).setValue(item.getDeathCount()).build());
                    }
                }
            } catch (Exception e) {
                log.warn("[StatisticalService] Failed to query monthly fluctuations: {}", e.getMessage());
            }
        }

        // 4. Build Age Structure response
        AgeStructureChart.Builder ageChartBuilder = AgeStructureChart.newBuilder();
        if (ageStructureData != null && ageStructureData.getTotalPopulation() > 0) {
            long total = ageStructureData.getTotalPopulation();
            ageChartBuilder.setCount014(ageStructureData.getPopulation014())
                    .setCount1564(ageStructureData.getPopulation1564())
                    .setCount65Plus(ageStructureData.getPopulation65Plus())
                    .setPct014(Math.round(((double) ageStructureData.getPopulation014() / total) * 1000.0) / 10.0)
                    .setPct1564(Math.round(((double) ageStructureData.getPopulation1564() / total) * 1000.0) / 10.0)
                    .setPct65Plus(Math.round(((double) ageStructureData.getPopulation65Plus() / total) * 1000.0) / 10.0);
        } else {
            ageChartBuilder.setCount014(0).setCount1564(0).setCount65Plus(0);
        }

        // 5. Build DashboardSummary card
        DashboardSummary summary = DashboardSummary.newBuilder()
                .setTotalPopulation(totalPopulation)
                .setTotalHouseholds(totalHouseholds)
                .setTemporaryResidents(12450)
                .setExpiringNationalIds(expiringIds)
                .setGrowthRatePct(1.2)
                .setUpdatedAt(LocalDateTime.now().toString())
                .build();

        return GetDashboardResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                .setSummary(summary)
                .setFluctuationChart(flucChartBuilder.build())
                .setAgeStructure(ageChartBuilder.build())
                .build();
    }

    @Override
    public GetPopulationTableResponse getPopulationTable(String parentAreaCode, int areaLevel, int page, int size) {
        int pageIdx = page > 0 ? page - 1 : 0;
        int pageSize = size > 0 ? size : 20;

        Page<PopulationStatistic> pageResult = statisticRepository.findByYearOrderByAreaCodeAsc(
                LocalDate.now().getYear(), PageRequest.of(pageIdx, pageSize));

        GetPopulationTableResponse.Builder respBuilder = GetPopulationTableResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                .setPage(PageInfo.newBuilder()
                        .setCurrentPage(pageResult.getNumber() + 1)
                        .setPageSize(pageResult.getSize())
                        .setTotalElements(pageResult.getTotalElements())
                        .setTotalPages(pageResult.getTotalPages())
                        .build());

        for (PopulationStatistic s : pageResult.getContent()) {
            respBuilder.addData(AreaStatisticsRow.newBuilder()
                    .setAreaCode(s.getAreaCode())
                    .setAreaName(s.getAreaName() != null ? s.getAreaName() : "")
                    .setPopulation(s.getTotalPopulation())
                    .setHouseholdCount(s.getHouseholdCount())
                    .setDensity(s.getPopulationDensity() != null ? s.getPopulationDensity() : 0.0)
                    .setStatus(1)
                    .setStatusLabel("Stable")
                    .build());
        }

        return respBuilder.build();
    }

    @Override
    @Transactional
    public PopulationStatistic recordStatistic(PopulationStatistic statistic) {
        return statisticRepository.save(statistic);
    }
}
