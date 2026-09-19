package vn.civilpro.household.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.civil.grpc.household.HouseholdInfo;
import vn.civil.grpc.household.HouseholdMemberInfo;
import vn.civilpro.household.model.dto.response.HouseholdDetailResponse;
import vn.civilpro.household.model.dto.response.HouseholdMemberResponse;
import vn.civilpro.household.model.entity.Household;
import vn.civilpro.household.model.entity.HouseholdMember;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HouseholdMapper {

    HouseholdDetailResponse toDetailResponse(Household household);

    HouseholdMemberResponse toMemberResponse(HouseholdMember member);

    List<HouseholdMemberResponse> toMemberResponseList(List<HouseholdMember> members);

    @Mapping(target = "registrationDate", expression = "java(household.getRegistrationDate() != null ? household.getRegistrationDate().toString() : \"\")")
    HouseholdInfo toProtoInfo(Household household);

    @Mapping(target = "joinDate", expression = "java(member.getJoinDate() != null ? member.getJoinDate().toString() : \"\")")
    @Mapping(target = "leaveDate", expression = "java(member.getLeaveDate() != null ? member.getLeaveDate().toString() : \"\")")
    HouseholdMemberInfo toProtoMemberInfo(HouseholdMember member);

    List<HouseholdMemberInfo> toProtoMemberInfoList(List<HouseholdMember> members);
}
