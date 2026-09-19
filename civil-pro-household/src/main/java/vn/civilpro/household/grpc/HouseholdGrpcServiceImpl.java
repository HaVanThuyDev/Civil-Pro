package vn.civilpro.household.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.civil.grpc.common.GrpcResponse;
import vn.civil.grpc.household.*;
import vn.civilpro.household.model.entity.HouseholdMember;
import vn.civilpro.household.mapper.HouseholdMapper;
import vn.civilpro.household.repository.HouseholdMemberRepository;
import vn.civilpro.household.repository.HouseholdRepository;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class HouseholdGrpcServiceImpl extends HouseholdGrpcServiceGrpc.HouseholdGrpcServiceImplBase {

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository memberRepository;
    private final HouseholdMapper householdMapper;

    @Override
    public void getById(GetHouseholdByIdRequest request, StreamObserver<GetHouseholdResponse> responseObserver) {
        try {
            var opt = householdRepository.findById(request.getId());
            if (opt.isEmpty()) {
                responseObserver.onNext(GetHouseholdResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage("Household not found").build())
                        .build());
            } else {
                responseObserver.onNext(GetHouseholdResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                        .setData(householdMapper.toProtoInfo(opt.get()))
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getById: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getByCode(GetHouseholdByCodeRequest request, StreamObserver<GetHouseholdResponse> responseObserver) {
        try {
            var opt = householdRepository.findByHouseholdCode(request.getHouseholdCode());
            if (opt.isEmpty()) {
                responseObserver.onNext(GetHouseholdResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage("Household not found").build())
                        .build());
            } else {
                responseObserver.onNext(GetHouseholdResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                        .setData(householdMapper.toProtoInfo(opt.get()))
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getByCode: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getHouseholdByCitizenId(GetHouseholdByCitizenIdRequest request, StreamObserver<GetHouseholdResponse> responseObserver) {
        try {
            var opt = householdRepository.findHouseholdByCitizenId(request.getCitizenId());
            if (opt.isEmpty()) {
                responseObserver.onNext(GetHouseholdResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(false).setCode(404).setMessage("Household not found for citizen").build())
                        .build());
            } else {
                responseObserver.onNext(GetHouseholdResponse.newBuilder()
                        .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                        .setData(householdMapper.toProtoInfo(opt.get()))
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getHouseholdByCitizenId: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getMembers(GetHouseholdMembersRequest request, StreamObserver<GetHouseholdMembersResponse> responseObserver) {
        try {
            List<HouseholdMember> members = request.getActiveOnly()
                    ? memberRepository.findByHouseholdIdAndStatus(request.getHouseholdId(), 1)
                    : memberRepository.findByHouseholdId(request.getHouseholdId());

            List<HouseholdMemberInfo> protoList = householdMapper.toProtoMemberInfoList(members);

            responseObserver.onNext(GetHouseholdMembersResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .addAllData(protoList)
                    .setTotal(protoList.size())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error getMembers: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void countHouseholdsByArea(CountHouseholdsByAreaRequest request, StreamObserver<CountHouseholdsResponse> responseObserver) {
        try {
            long count = householdRepository.countByAreaCode(request.getAreaCode());
            responseObserver.onNext(CountHouseholdsResponse.newBuilder()
                    .setMeta(GrpcResponse.newBuilder().setSuccess(true).setCode(200).setMessage("OK").build())
                    .setCount(count)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("[gRPC] Error countHouseholdsByArea: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
