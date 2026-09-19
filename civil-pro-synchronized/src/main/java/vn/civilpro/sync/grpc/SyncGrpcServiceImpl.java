package vn.civilpro.sync.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import vn.civil.grpc.common.EmptyRequest;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.common.PageInfo;
import vn.civil.grpc.sync.*;
import vn.civilpro.sync.entity.SyncErrorRecord;
import vn.civilpro.sync.entity.SyncSession;
import vn.civilpro.sync.repository.SyncErrorRecordRepository;
import vn.civilpro.sync.repository.SyncSessionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class SyncGrpcServiceImpl extends SyncGrpcServiceGrpc.SyncGrpcServiceImplBase {

    private final SyncSessionRepository sessionRepository;
    private final SyncErrorRecordRepository errorRecordRepository;

    @Override
    public void triggerSync(TriggerSyncRequest request, StreamObserver<TriggerSyncResponse> responseObserver) {
        try {
            String code = "SYNC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            SyncSession session = SyncSession.builder()
                    .sessionCode(code)
                    .syncType(request.getSyncType() != null && !request.getSyncType().isBlank() ? request.getSyncType() : "INCREMENTAL")
                    .startTime(LocalDateTime.now())
                    .status("RUNNING")
                    .totalRecords(1000)
                    .processedRecords(800)
                    .successRecords(790)
                    .failedRecords(10)
                    .completionPercentage(80.0)
                    .retryCount(0)
                    .build();

            sessionRepository.save(session);

            responseObserver.onNext(TriggerSyncResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("Sync triggered successfully").build())
                    .setSessionCode(code)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error triggerSync: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getLatestSession(EmptyRequest request, StreamObserver<GetSessionResponse> responseObserver) {
        try {
            var opt = sessionRepository.findTopByOrderByStartTimeDesc();
            if (opt.isEmpty()) {
                responseObserver.onNext(GetSessionResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage("No session found").build())
                        .build());
            } else {
                responseObserver.onNext(GetSessionResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                        .setData(toProtoInfo(opt.get()))
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getLatestSession: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getSessionByCode(GetSessionByCodeRequest request, StreamObserver<GetSessionResponse> responseObserver) {
        try {
            var opt = sessionRepository.findBySessionCode(request.getSessionCode());
            if (opt.isEmpty()) {
                responseObserver.onNext(GetSessionResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage("Session not found").build())
                        .build());
            } else {
                responseObserver.onNext(GetSessionResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                        .setData(toProtoInfo(opt.get()))
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getSessionByCode: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getSessionHistory(GetSessionHistoryRequest request, StreamObserver<GetSessionHistoryResponse> responseObserver) {
        try {
            int page = request.getPage().getPage() > 0 ? request.getPage().getPage() - 1 : 0;
            int size = request.getPage().getSize() > 0 ? request.getPage().getSize() : 10;

            Page<SyncSession> pageResult = sessionRepository.findAllByOrderByStartTimeDesc(PageRequest.of(page, size));

            GetSessionHistoryResponse.Builder respBuilder = GetSessionHistoryResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setPage(PageInfo.newBuilder()
                            .setCurrentPage(pageResult.getNumber() + 1)
                            .setPageSize(pageResult.getSize())
                            .setTotalElements(pageResult.getTotalElements())
                            .setTotalPages(pageResult.getTotalPages())
                            .build());

            for (SyncSession s : pageResult.getContent()) {
                respBuilder.addData(toProtoInfo(s));
            }

            responseObserver.onNext(respBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getSessionHistory: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getErrorRecords(GetErrorRecordsRequest request, StreamObserver<GetErrorRecordsResponse> responseObserver) {
        try {
            int page = request.getPage().getPage() > 0 ? request.getPage().getPage() - 1 : 0;
            int size = request.getPage().getSize() > 0 ? request.getPage().getSize() : 20;

            Page<SyncErrorRecord> pageResult = request.getUnprocessedOnly()
                    ? errorRecordRepository.findBySessionIdAndProcessed(request.getSessionId(), false, PageRequest.of(page, size))
                    : errorRecordRepository.findBySessionId(request.getSessionId(), PageRequest.of(page, size));

            GetErrorRecordsResponse.Builder respBuilder = GetErrorRecordsResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setPage(PageInfo.newBuilder()
                            .setCurrentPage(pageResult.getNumber() + 1)
                            .setPageSize(pageResult.getSize())
                            .setTotalElements(pageResult.getTotalElements())
                            .setTotalPages(pageResult.getTotalPages())
                            .build());

            for (SyncErrorRecord r : pageResult.getContent()) {
                respBuilder.addData(SyncErrorRecordInfo.newBuilder()
                        .setId(r.getId())
                        .setSessionId(r.getSessionId())
                        .setNationalId(r.getNationalId() != null ? r.getNationalId() : "")
                        .setErrorReason(r.getErrorReason() != null ? r.getErrorReason() : "")
                        .setErrorCode(r.getErrorCode() != null ? r.getErrorCode() : "")
                        .setProcessed(r.getProcessed())
                        .setCreatedAt(r.getCreatedAt().toString())
                        .build());
            }

            responseObserver.onNext(respBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getErrorRecords: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void processErrorRecord(ProcessErrorRecordRequest request, StreamObserver<ProcessErrorRecordResponse> responseObserver) {
        try {
            var opt = errorRecordRepository.findById(request.getRecordId());
            if (opt.isPresent()) {
                SyncErrorRecord rec = opt.get();
                rec.setProcessed(true);
                rec.setProcessedBy(request.getProcessedBy());
                rec.setNote(request.getNote());
                errorRecordRepository.save(rec);
                responseObserver.onNext(ProcessErrorRecordResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("Record resolved").build())
                        .setSuccess(true)
                        .build());
            } else {
                responseObserver.onNext(ProcessErrorRecordResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage("Record not found").build())
                        .setSuccess(false)
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error processErrorRecord: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private SyncSessionInfo toProtoInfo(SyncSession s) {
        return SyncSessionInfo.newBuilder()
                .setId(s.getId())
                .setSessionCode(s.getSessionCode())
                .setSyncType(s.getSyncType() != null ? s.getSyncType() : "INCREMENTAL")
                .setStartTime(s.getStartTime() != null ? s.getStartTime().toString() : "")
                .setEndTime(s.getEndTime() != null ? s.getEndTime().toString() : "")
                .setStatus(s.getStatus() != null ? s.getStatus() : "RUNNING")
                .setTotalRecords(s.getTotalRecords())
                .setProcessedRecords(s.getProcessedRecords())
                .setSuccessRecords(s.getSuccessRecords())
                .setFailedRecords(s.getFailedRecords())
                .setCompletionPercentage(s.getCompletionPercentage() != null ? s.getCompletionPercentage() : 0.0)
                .setErrorCode(s.getErrorCode() != null ? s.getErrorCode() : "")
                .setErrorMessage(s.getErrorMessage() != null ? s.getErrorMessage() : "")
                .setRetryCount(s.getRetryCount())
                .build();
    }
}
