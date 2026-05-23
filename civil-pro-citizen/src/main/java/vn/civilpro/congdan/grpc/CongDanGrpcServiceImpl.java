package vn.civilpro.congdan.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.civilpro.congdan.entity.CongDan;
import vn.civilpro.congdan.repository.CongDanRepository;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.congdan.*;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CongDanGrpcServiceImpl extends CongDanGrpcServiceGrpc.CongDanGrpcServiceImplBase {

    private final CongDanRepository congDanRepository;

    @Override
    public void getById(GetCongDanByIdRequest request,
                        StreamObserver<GetCongDanResponse> responseObserver) {
        try {
            log.debug("getById: id={}", request.getId());

            congDanRepository.findById(request.getId())
                    .ifPresentOrElse(
                            cd -> responseObserver.onNext(buildSuccessResponse(cd)),
                            () -> responseObserver.onNext(buildNotFoundResponse(request.getId()))
                    );

            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error(" getById error: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getByNationalId(GetCongDanByCccdRequest request,
                                StreamObserver<GetCongDanResponse> responseObserver) {
        try {
            log.debug("getByNationalId: nationalId={}", request.getNationalId());

            congDanRepository.findBySoCccd(request.getNationalId())
                    .ifPresentOrElse(
                            cd -> responseObserver.onNext(buildSuccessResponse(cd)),
                            () -> responseObserver.onNext(
                                    GetCongDanResponse.newBuilder()
                                            .setMeta(GrpcResponse.newBuilder()
                                                    .setSuccess(false).setCode(404)
                                                    .setMessage("National ID not found: " + request.getNationalId())
                                                    .build())
                                            .build())
                    );

            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("getByNationalId error: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getListByIds(GetCongDanListByIdsRequest request,
                             StreamObserver<GetCongDanListResponse> responseObserver) {
        try {
            log.debug(" getListByIds: count={}", request.getIdsCount());

            List<CongDan> list = congDanRepository.findByIdIn(request.getIdsList());

            GetCongDanListResponse.Builder builder = GetCongDanListResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder()
                            .setSuccess(true).setCode(200).setMessage("OK")
                            .build());

            list.forEach(cd -> builder.addData(mapToProto(cd)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error(" getListByIds error: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void checkNationalIdExist(CheckCccdExistRequest request,
                                     StreamObserver<CheckExistResponse> responseObserver) {
        try {
            boolean exists = request.getExcludeId() > 0
                    ? congDanRepository.existsBySoCccdAndIdNot(request.getNationalId(), request.getExcludeId())
                    : congDanRepository.existsBySoCccd(request.getNationalId());

            responseObserver.onNext(CheckExistResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setExists(exists)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[gRPC] checkNationalIdExist error: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    private GetCongDanResponse buildSuccessResponse(CongDan cd) {
        return GetCongDanResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                .setData(mapToProto(cd))
                .build();
    }

    private GetCongDanResponse buildNotFoundResponse(long id) {
        return GetCongDanResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder()
                        .setSuccess(false).setCode(404)
                        .setMessage("Citizen not found, ID: " + id)
                        .build())
                .build();
    }

    private CongDanInfo mapToProto(CongDan cd) {
        return CongDanInfo.newBuilder()
                .setId(cd.getId())
                .setCitizenCode(cd.getMaCongDan() != null ? cd.getMaCongDan() : "")
                .setFullName(cd.getHoTen())
                .setGender(cd.getGioiTinh())
                .setDateOfBirth(cd.getNgaySinh() != null ? cd.getNgaySinh().toString() : "")
                .setNationalId(cd.getSoCccd() != null ? cd.getSoCccd() : "")
                .setPermanentAddressCode(cd.getMaDvhcThuongTru() != null ? cd.getMaDvhcThuongTru() : "")
                .setPermanentAddress(cd.getDiaChiThuongTru() != null ? cd.getDiaChiThuongTru() : "")
                .setOccupation(cd.getNgheNghiep() != null ? cd.getNgheNghiep() : "")
                .setCitizenType(cd.getLoaiDoiTuong() != null ? cd.getLoaiDoiTuong() : "")
                .setStatus(cd.getTrangThai())
                .setIsHouseholdHead(Boolean.TRUE.equals(cd.getLaChuHo()))
                .setHouseholdId(cd.getIdHoKhau() != null ? cd.getIdHoKhau() : 0)
                .build();
    }
}