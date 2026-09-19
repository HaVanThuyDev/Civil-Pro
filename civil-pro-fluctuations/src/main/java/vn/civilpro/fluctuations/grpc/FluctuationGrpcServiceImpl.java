package vn.civilpro.fluctuations.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.common.PageInfo;
import vn.civil.grpc.fluctuation.*;
import vn.civilpro.fluctuations.model.entity.PopulationFluctuation;
import vn.civilpro.fluctuations.repository.PopulationFluctuationRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FluctuationGrpcServiceImpl extends FluctuationGrpcServiceGrpc.FluctuationGrpcServiceImplBase {

    private final PopulationFluctuationRepository fluctuationRepository;

    @Override
    public void getMonthlyFluctuations(GetMonthlyFluctuationsRequest request, StreamObserver<GetMonthlyFluctuationsResponse> responseObserver) {
        try {
            int year = request.getYear() > 0 ? request.getYear() : LocalDate.now().getYear();
            int fromMonth = request.getFromMonth() > 0 ? request.getFromMonth() : 1;
            int toMonth = request.getToMonth() > 0 ? request.getToMonth() : 12;

            List<Object[]> rows = fluctuationRepository.aggregateMonthlySummary(
                    request.getAreaCode(), year, fromMonth, toMonth);

            // Map: month -> (type -> count)
            Map<Integer, Map<String, Long>> monthTypeMap = new HashMap<>();
            for (Object[] r : rows) {
                Integer m = ((Number) r[0]).intValue();
                String type = (String) r[1];
                Long cnt = ((Number) r[2]).longValue();

                monthTypeMap.computeIfAbsent(m, k -> new HashMap<>()).put(type, cnt);
            }

            GetMonthlyFluctuationsResponse.Builder respBuilder = GetMonthlyFluctuationsResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build());

            for (int m = fromMonth; m <= toMonth; m++) {
                Map<String, Long> types = monthTypeMap.getOrDefault(m, Map.of());
                long births = types.getOrDefault("BIRTH", 0L);
                long deaths = types.getOrDefault("DEATH", 0L);
                long immigrations = types.getOrDefault("IMMIGRATION", 0L);
                long emigrations = types.getOrDefault("EMIGRATION", 0L);
                long growth = births - deaths + immigrations - emigrations;

                respBuilder.addData(MonthlyFluctuationSummary.newBuilder()
                        .setAreaCode(request.getAreaCode() != null ? request.getAreaCode() : "")
                        .setMonth(m)
                        .setYear(year)
                        .setBirthCount(births)
                        .setDeathCount(deaths)
                        .setImmigrationCount(immigrations)
                        .setEmigrationCount(emigrations)
                        .setGrowthCount(growth)
                        .build());
            }

            responseObserver.onNext(respBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getMonthlyFluctuations: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getFluctuations(GetFluctuationsRequest request, StreamObserver<GetFluctuationsResponse> responseObserver) {
        try {
            int page = request.getPage().getPage() > 0 ? request.getPage().getPage() - 1 : 0;
            int size = request.getPage().getSize() > 0 ? request.getPage().getSize() : 20;

            LocalDate from = request.hasDateRange() && !request.getDateRange().getFromDate().isBlank()
                    ? LocalDate.parse(request.getDateRange().getFromDate()) : null;
            LocalDate to = request.hasDateRange() && !request.getDateRange().getToDate().isBlank()
                    ? LocalDate.parse(request.getDateRange().getToDate()) : null;

            Page<PopulationFluctuation> pageResult = fluctuationRepository.search(
                    request.getAreaCode(), request.getFluctuationType(), from, to, PageRequest.of(page, size));

            GetFluctuationsResponse.Builder respBuilder = GetFluctuationsResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setPage(PageInfo.newBuilder()
                            .setCurrentPage(pageResult.getNumber() + 1)
                            .setPageSize(pageResult.getSize())
                            .setTotalElements(pageResult.getTotalElements())
                            .setTotalPages(pageResult.getTotalPages())
                            .build());

            for (PopulationFluctuation f : pageResult.getContent()) {
                respBuilder.addData(FluctuationInfo.newBuilder()
                        .setId(f.getId())
                        .setFluctuationCode(f.getFluctuationCode())
                        .setFluctuationType(f.getFluctuationType())
                        .setCitizenId(f.getCitizenId() != null ? f.getCitizenId() : 0L)
                        .setFullName(f.getFullName() != null ? f.getFullName() : "")
                        .setAreaCode(f.getAreaCode())
                        .setFluctuationDate(f.getFluctuationDate().toString())
                        .setMonth(f.getMonth())
                        .setYear(f.getYear())
                        .setDescription(f.getDescription() != null ? f.getDescription() : "")
                        .build());
            }

            responseObserver.onNext(respBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getFluctuations: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAnnualBirthDeath(GetAnnualBirthDeathRequest request, StreamObserver<GetAnnualBirthDeathResponse> responseObserver) {
        try {
            int year = request.getYear() > 0 ? request.getYear() : LocalDate.now().getYear();
            List<Object[]> results = fluctuationRepository.countAnnualBirthDeath(request.getAreaCode(), year);

            long births = 0;
            long deaths = 0;
            for (Object[] r : results) {
                String type = (String) r[0];
                long cnt = ((Number) r[1]).longValue();
                if ("BIRTH".equalsIgnoreCase(type)) {
                    births = cnt;
                } else if ("DEATH".equalsIgnoreCase(type)) {
                    deaths = cnt;
                }
            }

            responseObserver.onNext(GetAnnualBirthDeathResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setTotalBirths(births)
                    .setTotalDeaths(deaths)
                    .setYear(year)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getAnnualBirthDeath: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
