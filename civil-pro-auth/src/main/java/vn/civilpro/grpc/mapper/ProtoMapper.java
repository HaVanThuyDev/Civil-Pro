package vn.civilpro.grpc.mapper;

import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.dto.response.AuthResponse;
import vn.civilpro.auth.grpc.proto.UserInfo;
import vn.civilpro.model.entity.Role;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtoMapper {

    public static vn.civilpro.auth.grpc.proto.AuthResponse toAuthProto(AuthResponse dto) {
        if (dto == null) return vn.civilpro.auth.grpc.proto.AuthResponse.getDefaultInstance();

        return vn.civilpro.auth.grpc.proto.AuthResponse.newBuilder()
                .setAccessToken(nullSafe(dto.getAccessToken()))
                .setRefreshToken(nullSafe(dto.getRefreshToken()))
                .setExpiresIn(dto.getExpiresIn() == null ? 0L : dto.getExpiresIn())
                .setTokenType(nullSafe(dto.getTokenType()))
                .setUser(toUserInfoProto(dtoToUserDto(dto))) // Luồng dữ liệu chạy mượt mà qua các hàm trung gian
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

        // SỬA LỖI & TỐI ƯU: Chuyển Set<Role> sang tập hợp mã chuỗi gọn gàng
        if (dto.getRoles() != null) {
            builder.addAllRoleCodes(dto.getRoles().stream()
                    .filter(role -> role != null && role.getRoleCode() != null)
                    .map(Role::getRoleCode)
                    .collect(Collectors.toList()));
        }

        // TỐI ƯU: Nạp thẳng Set<String> vào danh sách lặp của gRPC
        if (dto.getAuthorities() != null) {
            builder.addAllAuthorities(dto.getAuthorities().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    private static UserDto dtoToUserDto(AuthResponse dto) {
        if (dto == null) return null;

        return UserDto.builder()
                .id(dto.getUserId())
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .administrativeUnitCode(dto.getAdministrativeUnitCode())
                .roles(dto.getRoles() == null ? null : dto.getRoles().stream()
                        .filter(Objects::nonNull)
                        .map(roleCode -> Role.builder().roleCode(roleCode).build())
                        .collect(Collectors.toSet()))
                .authorities(dto.getAuthorities())
                .build();
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}