package vn.civilpro.congdan.mapper;

import org.mapstruct.*;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.request.UpdateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.dto.response.CongDanSummaryResponse;
import vn.civilpro.congdan.entity.CongDan;

import java.time.LocalDate;
import java.time.Period;

/**
 * ================================================================
 * MAPSTRUCT MAPPER - CÔNG DÂN
 * Tự động generate implementation lúc compile.
 * componentModel = "spring" → inject được bằng @Autowired / @RequiredArgsConstructor
 * ================================================================
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CongDanMapper {

    // ---- CreateRequest → Entity ----
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maCongDan", ignore = true)         // Sinh trong Service
    @Mapping(target = "hoTenKhongDau", ignore = true)     // Sinh trong Service
    @Mapping(target = "trangThai", constant = "HOAT_DONG")
    @Mapping(target = "laChuHo", constant = "false")
    @Mapping(target = "version", constant = "0")
    CongDan toEntity(CreateCongDanRequest request);

    // ---- UpdateRequest → Entity (chỉ update field != null) ----
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCongDanRequest request, @MappingTarget CongDan entity);

    // ---- Entity → DetailResponse ----
    @Mapping(target = "gioiTinhLabel", expression = "java(mapGioiTinh(congDan.getGioiTinh()))")
    @Mapping(target = "tuoi", expression = "java(tinhTuoi(congDan.getNgaySinh()))")
    @Mapping(target = "trangThaiLabel", expression = "java(mapTrangThai(congDan.getTrangThai()))")
    @Mapping(target = "cccdSapHetHan", expression = "java(isCccdSapHetHan(congDan.getNgayHetHanCccd()))")
    CongDanDetailResponse toDetailResponse(CongDan congDan);

    // ---- Entity → SummaryResponse ----
    @Mapping(target = "gioiTinhLabel", expression = "java(mapGioiTinh(congDan.getGioiTinh()))")
    @Mapping(target = "tuoi", expression = "java(tinhTuoi(congDan.getNgaySinh()))")
    @Mapping(target = "trangThaiLabel", expression = "java(mapTrangThai(congDan.getTrangThai()))")
    CongDanSummaryResponse toSummaryResponse(CongDan congDan);

    // ---- Default methods (dùng trong @Mapping expression) ----

    default String mapGioiTinh(Integer gioiTinh) {
        if (gioiTinh == null) return "";
        return switch (gioiTinh) {
            case 1 -> "Nam";
            case 2 -> "Nữ";
            default -> "Khác";
        };
    }

    default Integer tinhTuoi(LocalDate ngaySinh) {
        if (ngaySinh == null) return null;
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    default String mapTrangThai(String trangThai) {
        if (trangThai == null) return "";
        return switch (trangThai) {
            case "HOAT_DONG" -> "Đang hoạt động";
            case "DA_CHET"   -> "Đã khai tử";
            case "XUAT_CANH" -> "Đã xuất cảnh";
            default          -> trangThai;
        };
    }

    default boolean isCccdSapHetHan(LocalDate ngayHetHan) {
        if (ngayHetHan == null) return false;
        // Cảnh báo nếu còn dưới 90 ngày
        return LocalDate.now().plusDays(90).isAfter(ngayHetHan);
    }
}