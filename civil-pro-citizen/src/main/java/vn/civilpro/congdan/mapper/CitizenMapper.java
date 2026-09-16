package vn.civilpro.congdan.mapper;


import org.mapstruct.*;
import vn.civilpro.congdan.dto.request.CreateCitizenRequest;
import vn.civilpro.congdan.dto.request.UpdateCitizenRequest;
import vn.civilpro.congdan.dto.response.CitizenDetailResponse;
import vn.civilpro.congdan.dto.response.CitizenSummaryResponse;
import vn.civilpro.congdan.entity.Citizen;

import java.time.LocalDate;
import java.time.Period;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CitizenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "citizenCode", ignore = true)
    @Mapping(target = "fullNameAscii", ignore = true)
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "isHouseholdHead", constant = "0")
    @Mapping(target = "version", constant = "0")
    Citizen toEntity(CreateCitizenRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCitizenRequest request, @MappingTarget Citizen entity);

    @Mapping(target = "genderLabel", expression = "java(mapGender(citizen.getGender()))")
    @Mapping(target = "age", expression = "java(calculateAge(citizen.getDateOfBirth()))")
    @Mapping(target = "statusLabel", expression = "java(mapStatus(citizen.getStatus()))")
    @Mapping(target = "idCardExpiringSoon", expression = "java(isIdCardExpiringSoon(citizen.getIdCardExpiryDate()))")
    @Mapping(target = "isHouseholdHead", expression = "java(citizen.getIsHouseholdHead() != null && citizen.getIsHouseholdHead() == 1)")
    CitizenDetailResponse toDetailResponse(Citizen citizen);

    @Mapping(target = "genderLabel", expression = "java(mapGender(citizen.getGender()))")
    @Mapping(target = "age", expression = "java(calculateAge(citizen.getDateOfBirth()))")
    @Mapping(target = "statusLabel", expression = "java(mapStatus(citizen.getStatus()))")
    CitizenSummaryResponse toSummaryResponse(Citizen citizen);

    default String mapGender(Integer gender) {
        if (gender == null) return "";
        return switch (gender) {
            case 1 -> "Male";
            case 2 -> "Female";
            default -> "Other";
        };
    }

    default Integer calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return null;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    default String mapStatus(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 1 -> "Active";
            case 0 -> "Deceased";
            case 2 -> "Emigrated";
            default -> String.valueOf(status);
        };
    }

    default boolean isIdCardExpiringSoon(LocalDate expiryDate) {
        if (expiryDate == null) return false;
        return LocalDate.now().plusDays(90).isAfter(expiryDate);
    }
}