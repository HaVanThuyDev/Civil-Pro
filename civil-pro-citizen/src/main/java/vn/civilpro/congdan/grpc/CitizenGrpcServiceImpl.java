package vn.civilpro.congdan.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.civil.grpc.citizen.*;
import vn.civil.grpc.common.GrpcResponse;
import vn.civilpro.congdan.model.entity.Citizen;
import vn.civilpro.congdan.repository.CitizenRepository;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CitizenGrpcServiceImpl extends CitizenGrpcServiceGrpc.CitizenGrpcServiceImplBase {

    private final CitizenRepository citizenRepository;


    @Override
    public void getById(GetCitizenByIdRequest request,
                        StreamObserver<GetCitizenResponse> responseObserver) {
        try {
            log.debug("getById: id={}", request.getId());

            citizenRepository.findById(request.getId())
                    .ifPresentOrElse(
                            citizen -> responseObserver.onNext(buildSuccessResponse(citizen)),
                            () -> responseObserver.onNext(buildNotFoundResponse(request.getId()))
                    );

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("getById error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getByNationalId(GetCitizenByNationalIdRequest request,
                                StreamObserver<GetCitizenResponse> responseObserver) {
        try {
            log.debug("getByNationalId: nationalId={}", request.getNationalId());

            citizenRepository.findByIdCardNumber(request.getNationalId())
                    .ifPresentOrElse(
                            citizen -> responseObserver.onNext(buildSuccessResponse(citizen)),
                            () -> responseObserver.onNext(
                                    GetCitizenResponse.newBuilder()
                                            .setMeta(GrpcResponse.newBuilder()
                                                    .setSuccess(false).setCode(404)
                                                    .setMessage("National ID not found: " + request.getNationalId())
                                                    .build())
                                            .build())
                    );

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("getByNationalId error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getListByIds(GetCitizenListByIdsRequest request,
                             StreamObserver<GetCitizenListResponse> responseObserver) {
        try {
            log.debug("getListByIds: count={}", request.getIdsCount());

            List<Citizen> list = citizenRepository.findByIdIn(request.getIdsList());

            GetCitizenListResponse.Builder builder = GetCitizenListResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder()
                            .setSuccess(true).setCode(200).setMessage("OK")
                            .build());

            list.forEach(citizen -> builder.addData(mapToSummaryProto(citizen)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("getListByIds error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void checkNationalIdExists(CheckNationalIdExistsRequest request,
                                      StreamObserver<CheckExistsResponse> responseObserver) {
        try {
            boolean exists = request.getExcludeId() > 0
                    ? citizenRepository.existsByIdCardNumberAndIdNot(request.getNationalId(), request.getExcludeId())
                    : citizenRepository.existsByIdCardNumber(request.getNationalId());

            responseObserver.onNext(CheckExistsResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setExists(exists)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("checkNationalIdExists error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private GetCitizenResponse buildSuccessResponse(Citizen citizen) {
        return GetCitizenResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                .setData(mapToInfoProto(citizen))
                .build();
    }

    private GetCitizenResponse buildNotFoundResponse(long id) {
        return GetCitizenResponse.newBuilder()
                .setMeta(GrpcResponse.newBuilder()
                        .setSuccess(false).setCode(404)
                        .setMessage("Citizen not found with ID: " + id)
                        .build())
                .build();
    }

    private CitizenInfo mapToInfoProto(Citizen citizen) {
        return CitizenInfo.newBuilder()
                .setId(citizen.getId())
                .setCitizenCode(citizen.getCitizenCode() != null ? citizen.getCitizenCode() : "")
                .setFullName(citizen.getFullName() != null ? citizen.getFullName() : "")
                .setGender(citizen.getGender() != null ? citizen.getGender() : 0)
                .setDateOfBirth(citizen.getDateOfBirth() != null ? citizen.getDateOfBirth().toString() : "")
                .setNationalId(citizen.getIdCardNumber() != null ? citizen.getIdCardNumber() : "")
                .setNationalIdExpiryDate(citizen.getIdCardExpiryDate() != null ? citizen.getIdCardExpiryDate().toString() : "")
                .setPermanentAddressCode(citizen.getPermanentAreaCode() != null ? citizen.getPermanentAreaCode() : "")
                .setPermanentAddress(citizen.getPermanentAddress() != null ? citizen.getPermanentAddress() : "")
                .setOccupation(citizen.getOccupation() != null ? citizen.getOccupation() : "")
                .setCitizenType(citizen.getCitizenType() != null ? citizen.getCitizenType() : "")
                .setStatus(citizen.getStatus() != null ? citizen.getStatus() : 0)
                .setIsHouseholdHead(citizen.getIsHouseholdHead() != null && citizen.getIsHouseholdHead() == 1)
                .setHouseholdId(citizen.getHouseholdId() != null ? citizen.getHouseholdId() : 0L)
                .setAge(citizen.getDateOfBirth() != null ? java.time.Period.between(citizen.getDateOfBirth(), java.time.LocalDate.now()).getYears() : 0)
                .build();
    }

    private CitizenSummary mapToSummaryProto(Citizen citizen) {
        return CitizenSummary.newBuilder()
                .setId(citizen.getId())
                .setCitizenCode(citizen.getCitizenCode() != null ? citizen.getCitizenCode() : "")
                .setFullName(citizen.getFullName() != null ? citizen.getFullName() : "")
                .setGender(citizen.getGender() != null ? citizen.getGender() : 0)
                .setDateOfBirth(citizen.getDateOfBirth() != null ? citizen.getDateOfBirth().toString() : "")
                .setNationalId(citizen.getIdCardNumber() != null ? citizen.getIdCardNumber() : "")
                .setStatus(citizen.getStatus() != null ? citizen.getStatus() : 0)
                .setAddressCode(citizen.getPermanentAreaCode() != null ? citizen.getPermanentAreaCode() : "")
                .build();
    }
}