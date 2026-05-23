package vn.civilpro.congdan.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.request.UpdateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.dto.response.CongDanSummaryResponse;
import vn.civilpro.congdan.entity.CongDan;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-23T22:41:33+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class CongDanMapperImpl implements CongDanMapper {

    @Override
    public CongDan toEntity(CreateCongDanRequest request) {
        if ( request == null ) {
            return null;
        }

        CongDan.CongDanBuilder congDan = CongDan.builder();

        congDan.hoTen( mapTrangThai( request.getHoTen() ) );
        congDan.gioiTinh( request.getGioiTinh() );
        congDan.ngaySinh( request.getNgaySinh() );
        congDan.noiSinh( mapTrangThai( request.getNoiSinh() ) );
        congDan.danToc( mapTrangThai( request.getDanToc() ) );
        congDan.tonGiao( mapTrangThai( request.getTonGiao() ) );
        congDan.soCccd( mapTrangThai( request.getSoCccd() ) );
        congDan.ngayCapCccd( request.getNgayCapCccd() );
        congDan.noiCapCccd( mapTrangThai( request.getNoiCapCccd() ) );
        congDan.ngayHetHanCccd( request.getNgayHetHanCccd() );
        congDan.soDienThoai( mapTrangThai( request.getSoDienThoai() ) );
        congDan.email( mapTrangThai( request.getEmail() ) );
        congDan.maDvhcThuongTru( mapTrangThai( request.getMaDvhcThuongTru() ) );
        congDan.diaChiThuongTru( mapTrangThai( request.getDiaChiThuongTru() ) );
        congDan.ngheNghiep( mapTrangThai( request.getNgheNghiep() ) );
        congDan.trinhDoHocVan( mapTrangThai( request.getTrinhDoHocVan() ) );
        congDan.noiLamViec( mapTrangThai( request.getNoiLamViec() ) );
        congDan.loaiDoiTuong( mapTrangThai( request.getLoaiDoiTuong() ) );

        congDan.trangThai( mapTrangThai( "HOAT_DONG" ) );
        congDan.laChuHo( false );
        congDan.version( 0 );

        return congDan.build();
    }

    @Override
    public void updateEntityFromRequest(UpdateCongDanRequest request, CongDan entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getHoTen() != null ) {
            entity.setHoTen( mapTrangThai( request.getHoTen() ) );
        }
        if ( request.getGioiTinh() != null ) {
            entity.setGioiTinh( request.getGioiTinh() );
        }
        if ( request.getNgaySinh() != null ) {
            entity.setNgaySinh( request.getNgaySinh() );
        }
        if ( request.getNoiSinh() != null ) {
            entity.setNoiSinh( mapTrangThai( request.getNoiSinh() ) );
        }
        if ( request.getDanToc() != null ) {
            entity.setDanToc( mapTrangThai( request.getDanToc() ) );
        }
        if ( request.getTonGiao() != null ) {
            entity.setTonGiao( mapTrangThai( request.getTonGiao() ) );
        }
        if ( request.getSoCccd() != null ) {
            entity.setSoCccd( mapTrangThai( request.getSoCccd() ) );
        }
        if ( request.getNgayCapCccd() != null ) {
            entity.setNgayCapCccd( request.getNgayCapCccd() );
        }
        if ( request.getNoiCapCccd() != null ) {
            entity.setNoiCapCccd( mapTrangThai( request.getNoiCapCccd() ) );
        }
        if ( request.getNgayHetHanCccd() != null ) {
            entity.setNgayHetHanCccd( request.getNgayHetHanCccd() );
        }
        if ( request.getSoDienThoai() != null ) {
            entity.setSoDienThoai( mapTrangThai( request.getSoDienThoai() ) );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( mapTrangThai( request.getEmail() ) );
        }
        if ( request.getMaDvhcThuongTru() != null ) {
            entity.setMaDvhcThuongTru( mapTrangThai( request.getMaDvhcThuongTru() ) );
        }
        if ( request.getDiaChiThuongTru() != null ) {
            entity.setDiaChiThuongTru( mapTrangThai( request.getDiaChiThuongTru() ) );
        }
        if ( request.getNgheNghiep() != null ) {
            entity.setNgheNghiep( mapTrangThai( request.getNgheNghiep() ) );
        }
        if ( request.getTrinhDoHocVan() != null ) {
            entity.setTrinhDoHocVan( mapTrangThai( request.getTrinhDoHocVan() ) );
        }
        if ( request.getNoiLamViec() != null ) {
            entity.setNoiLamViec( mapTrangThai( request.getNoiLamViec() ) );
        }
        if ( request.getLoaiDoiTuong() != null ) {
            entity.setLoaiDoiTuong( mapTrangThai( request.getLoaiDoiTuong() ) );
        }
    }

    @Override
    public CongDanDetailResponse toDetailResponse(CongDan congDan) {
        if ( congDan == null ) {
            return null;
        }

        CongDanDetailResponse.CongDanDetailResponseBuilder congDanDetailResponse = CongDanDetailResponse.builder();

        congDanDetailResponse.id( congDan.getId() );
        congDanDetailResponse.maCongDan( mapTrangThai( congDan.getMaCongDan() ) );
        congDanDetailResponse.hoTen( mapTrangThai( congDan.getHoTen() ) );
        congDanDetailResponse.ngaySinh( congDan.getNgaySinh() );
        congDanDetailResponse.noiSinh( mapTrangThai( congDan.getNoiSinh() ) );
        congDanDetailResponse.danToc( mapTrangThai( congDan.getDanToc() ) );
        congDanDetailResponse.tonGiao( mapTrangThai( congDan.getTonGiao() ) );
        congDanDetailResponse.quocTich( mapTrangThai( congDan.getQuocTich() ) );
        congDanDetailResponse.soCccd( mapTrangThai( congDan.getSoCccd() ) );
        congDanDetailResponse.ngayCapCccd( congDan.getNgayCapCccd() );
        congDanDetailResponse.noiCapCccd( mapTrangThai( congDan.getNoiCapCccd() ) );
        congDanDetailResponse.ngayHetHanCccd( congDan.getNgayHetHanCccd() );
        congDanDetailResponse.soDienThoai( mapTrangThai( congDan.getSoDienThoai() ) );
        congDanDetailResponse.email( mapTrangThai( congDan.getEmail() ) );
        congDanDetailResponse.maDvhcThuongTru( mapTrangThai( congDan.getMaDvhcThuongTru() ) );
        congDanDetailResponse.diaChiThuongTru( mapTrangThai( congDan.getDiaChiThuongTru() ) );
        congDanDetailResponse.maDvhcTamTru( mapTrangThai( congDan.getMaDvhcTamTru() ) );
        congDanDetailResponse.diaChiTamTru( mapTrangThai( congDan.getDiaChiTamTru() ) );
        congDanDetailResponse.ngheNghiep( mapTrangThai( congDan.getNgheNghiep() ) );
        congDanDetailResponse.trinhDoHocVan( mapTrangThai( congDan.getTrinhDoHocVan() ) );
        congDanDetailResponse.noiLamViec( mapTrangThai( congDan.getNoiLamViec() ) );
        congDanDetailResponse.loaiDoiTuong( mapTrangThai( congDan.getLoaiDoiTuong() ) );
        congDanDetailResponse.laChuHo( congDan.getLaChuHo() );
        congDanDetailResponse.idHoKhau( congDan.getIdHoKhau() );
        congDanDetailResponse.trangThai( mapTrangThai( congDan.getTrangThai() ) );
        congDanDetailResponse.ngayKhaiTu( congDan.getNgayKhaiTu() );
        congDanDetailResponse.ngayTao( congDan.getNgayTao() );
        congDanDetailResponse.ngayCapNhat( congDan.getNgayCapNhat() );
        congDanDetailResponse.nguoiTao( mapTrangThai( congDan.getNguoiTao() ) );
        congDanDetailResponse.nguoiCapNhat( mapTrangThai( congDan.getNguoiCapNhat() ) );

        congDanDetailResponse.gioiTinhLabel( mapGioiTinh(congDan.getGioiTinh()) );
        congDanDetailResponse.tuoi( tinhTuoi(congDan.getNgaySinh()) );
        congDanDetailResponse.trangThaiLabel( mapTrangThai(congDan.getTrangThai()) );
        congDanDetailResponse.cccdSapHetHan( isCccdSapHetHan(congDan.getNgayHetHanCccd()) );

        return congDanDetailResponse.build();
    }

    @Override
    public CongDanSummaryResponse toSummaryResponse(CongDan congDan) {
        if ( congDan == null ) {
            return null;
        }

        CongDanSummaryResponse.CongDanSummaryResponseBuilder congDanSummaryResponse = CongDanSummaryResponse.builder();

        congDanSummaryResponse.id( congDan.getId() );
        congDanSummaryResponse.maCongDan( mapTrangThai( congDan.getMaCongDan() ) );
        congDanSummaryResponse.hoTen( mapTrangThai( congDan.getHoTen() ) );
        congDanSummaryResponse.ngaySinh( congDan.getNgaySinh() );
        congDanSummaryResponse.soCccd( mapTrangThai( congDan.getSoCccd() ) );
        congDanSummaryResponse.diaChiThuongTru( mapTrangThai( congDan.getDiaChiThuongTru() ) );
        congDanSummaryResponse.ngheNghiep( mapTrangThai( congDan.getNgheNghiep() ) );
        congDanSummaryResponse.loaiDoiTuong( mapTrangThai( congDan.getLoaiDoiTuong() ) );
        congDanSummaryResponse.trangThai( mapTrangThai( congDan.getTrangThai() ) );

        congDanSummaryResponse.gioiTinhLabel( mapGioiTinh(congDan.getGioiTinh()) );
        congDanSummaryResponse.tuoi( tinhTuoi(congDan.getNgaySinh()) );
        congDanSummaryResponse.trangThaiLabel( mapTrangThai(congDan.getTrangThai()) );

        return congDanSummaryResponse.build();
    }
}
