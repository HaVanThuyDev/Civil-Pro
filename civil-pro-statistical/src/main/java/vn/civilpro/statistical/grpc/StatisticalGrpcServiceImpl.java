package vn.civilpro.statistical.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.statistical.*;
import vn.civilpro.statistical.service.StatisticalService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class StatisticalGrpcServiceImpl extends StatisticalGrpcServiceGrpc.StatisticalGrpcServiceImplBase {

    private final StatisticalService statisticalService;

    @Override
    public void getDashboard(GetDashboardRequest request, StreamObserver<GetDashboardResponse> responseObserver) {
        try {
            GetDashboardResponse response = statisticalService.getDashboard(request.getAreaCode());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getDashboard: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getPopulationTable(GetPopulationTableRequest request, StreamObserver<GetPopulationTableResponse> responseObserver) {
        try {
            int page = request.getPage().getPage();
            int size = request.getPage().getSize();
            GetPopulationTableResponse response = statisticalService.getPopulationTable(
                    request.getParentAreaCode(), request.getAreaLevel(), page, size);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getPopulationTable: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void requestReportExport(RequestReportExportRequest request, StreamObserver<RequestReportExportResponse> responseObserver) {
        try {
            String reportCode = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            responseObserver.onNext(RequestReportExportResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("Report requested").build())
                    .setReportCode(reportCode)
                    .setStatus("PROCESSING")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error requestReportExport: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getReportResult(GetReportResultRequest request, StreamObserver<GetReportResultResponse> responseObserver) {
        try {
            ReportHistoryInfo report = ReportHistoryInfo.newBuilder()
                    .setId(1L)
                    .setReportCode(request.getReportCode())
                    .setReportType("POPULATION")
                    .setFormat("PDF")
                    .setStatus("COMPLETED")
                    .setRequestedBy("ADMIN")
                    .setRequestedAt(LocalDateTime.now().minusMinutes(5).toString())
                    .setCompletedAt(LocalDateTime.now().toString())
                    .setFileUrl("https://storage.civilpro.vn/reports/" + request.getReportCode() + ".pdf")
                    .build();

            responseObserver.onNext(GetReportResultResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setReport(report)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getReportResult: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
