package vn.civilpro.grpc.mapper;

import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.dto.response.AuthResponse;
import vn.civilpro.auth.grpc.proto.UserInfo;


public class ProtoMapper {

    public static vn.civilpro.auth.grpc.proto.AuthResponse toAuthProto(AuthResponse dto) {
        if (dto == null) return vn.civilpro.auth.grpc.proto.AuthResponse.getDefaultInstance();

        return vn.civilpro.auth.grpc.proto.AuthResponse.newBuilder()
                .setAccessToken(nullSafe(dto.getAccessToken()))
                .setRefreshToken(nullSafe(dto.getRefreshToken()))
                .setExpiresIn(dto.getExpiresIn() == null ? 0L : dto.getExpiresIn())
                .setTokenType(nullSafe(dto.getTokenType()))
                .setUser(toUserInfoProto(dtoToUserDto(dto)))
                .build();
    }

    public static UserInfo toUserInfoProto(UserDto dto) {
        if (dto == null) return UserInfo.getDefaultInstance();

        UserInfo.Builder builder = UserInfo.newBuilder()
                .setId(dto.getId() == null ? 0L : dto.getId())
                .setUsername(nullSafe(dto.getUsername()))
                .setFullName(nullSafe(dto.getFullName()))
                .setEmail(nullSafe(dto.getEmail()))
                .setPhoneNumber(nullSafe(dto.getPhoneNumber()))
                .setAdministrativeCode(nullSafe(dto.getAdministrativeUnitCode()))
                .setStatus(dto.getStatus() == null ? 0 : dto.getStatus());

        if (dto.getRoles() != null) {
            dto.getRoles().forEach(role -> builder.addRoleCodes(nullSafe(role)));
        }

        if (dto.getAuthorities() != null) {
            dto.getAuthorities().forEach(auth -> builder.addAuthorities(nullSafe(auth)));
        }

        return builder.build();
    }

    private static UserDto dtoToUserDto(AuthResponse dto) {
        return UserDto.builder()
                .id(dto.getUserId())
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .administrativeUnitCode(dto.getAdministrativeUnitCode())
                .roles(dto.getRoles())
                .authorities(dto.getAuthorities())
                .build();
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
