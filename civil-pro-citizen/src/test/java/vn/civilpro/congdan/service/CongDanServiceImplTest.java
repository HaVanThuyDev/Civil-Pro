package vn.civilpro.congdan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.civilpro.common.enums.ErrorCode;
import vn.civilpro.common.exception.DuplicateResourceException;
import vn.civilpro.common.exception.ResourceNotFoundException;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.entity.CongDan;
import vn.civilpro.congdan.event.CongDanEventPublisher;
import vn.civilpro.congdan.mapper.CongDanMapper;
import vn.civilpro.congdan.repository.CongDanRepository;
import vn.civilpro.congdan.service.impl.CongDanServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ================================================================
 * UNIT TEST: CONG DAN SERVICE
 * Test isolation: mock tất cả dependencies
 * ================================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CongDanService Unit Tests")
class CongDanServiceImplTest {

    @Mock
    private CongDanRepository congDanRepository;

    @Mock
    private CongDanMapper congDanMapper;

    @Mock
    private CongDanEventPublisher eventPublisher;

    @InjectMocks
    private CongDanServiceImpl congDanService;

    // ---- Test fixtures ----

    private CreateCongDanRequest validRequest;
    private CongDan savedEntity;
    private CongDanDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        validRequest = new CreateCongDanRequest();
        validRequest.setHoTen("Nguyễn Văn A");
        validRequest.setGioiTinh(1);
        validRequest.setNgaySinh(LocalDate.of(1990, 5, 15));
        validRequest.setSoCccd("038090012345");
        validRequest.setMaDvhcThuongTru("TP-002");
        validRequest.setDiaChiThuongTru("Số 12, Đường 3/2, P.1");

        savedEntity = CongDan.builder()
                .id(1L)
                .maCongDan("CD-100001")
                .hoTen("Nguyễn Văn A")
                .gioiTinh(1)
                .ngaySinh(LocalDate.of(1990, 5, 15))
                .soCccd("038090012345")
                .maDvhcThuongTru("TP-002")
                .trangThai("HOAT_DONG")
                .build();

        detailResponse = CongDanDetailResponse.builder()
                .id(1L)
                .maCongDan("CD-100001")
                .hoTen("Nguyễn Văn A")
                .trangThai("HOAT_DONG")
                .build();
    }

    // ================================================================
    // TEST GROUP: CREATE
    // ================================================================

    @Nested
    @DisplayName("Tạo công dân mới")
    class CreateTests {

        @Test
        @DisplayName("Tạo thành công khi dữ liệu hợp lệ")
        void create_success_whenValidRequest() {
            // Arrange
            when(congDanRepository.existsBySoCccd("038090012345")).thenReturn(false);
            when(congDanMapper.toEntity(any())).thenReturn(savedEntity);
            when(congDanRepository.save(any())).thenReturn(savedEntity);
            when(congDanMapper.toDetailResponse(any())).thenReturn(detailResponse);
            doNothing().when(eventPublisher).publishCongDanCreated(any());

            // Act
            CongDanDetailResponse result = congDanService.create(validRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMaCongDan()).isEqualTo("CD-100001");

            verify(congDanRepository).existsBySoCccd("038090012345");
            verify(congDanRepository).save(any(CongDan.class));
            verify(eventPublisher).publishCongDanCreated(any(CongDan.class));
        }

        @Test
        @DisplayName("Ném DuplicateResourceException khi CCCD đã tồn tại")
        void create_throwsDuplicateException_whenCccdExists() {
            // Arrange
            when(congDanRepository.existsBySoCccd("038090012345")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> congDanService.create(validRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .extracting(ex -> ((DuplicateResourceException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.CD_CCCD_EXISTS);

            verify(congDanRepository, never()).save(any());
            verify(eventPublisher, never()).publishCongDanCreated(any());
        }

        @Test
        @DisplayName("Không kiểm tra CCCD khi request không có CCCD")
        void create_skipCccdCheck_whenNoCccd() {
            // Arrange
            validRequest.setSoCccd(null);
            when(congDanMapper.toEntity(any())).thenReturn(savedEntity);
            when(congDanRepository.save(any())).thenReturn(savedEntity);
            when(congDanMapper.toDetailResponse(any())).thenReturn(detailResponse);
            doNothing().when(eventPublisher).publishCongDanCreated(any());

            // Act
            congDanService.create(validRequest);

            // Assert: không gọi existsBySoCccd khi cccd null
            verify(congDanRepository, never()).existsBySoCccd(any());
        }
    }

    // ================================================================
    // TEST GROUP: GET BY ID
    // ================================================================

    @Nested
    @DisplayName("Lấy thông tin công dân theo ID")
    class GetByIdTests {

        @Test
        @DisplayName("Trả về response khi tìm thấy")
        void getById_returnsResponse_whenFound() {
            // Arrange
            when(congDanRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
            when(congDanMapper.toDetailResponse(savedEntity)).thenReturn(detailResponse);

            // Act
            CongDanDetailResponse result = congDanService.getById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Ném ResourceNotFoundException khi không tìm thấy")
        void getById_throwsNotFoundException_whenNotFound() {
            // Arrange
            when(congDanRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> congDanService.getById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .extracting(ex -> ((ResourceNotFoundException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.CD_NOT_FOUND);
        }
    }

    // ================================================================
    // TEST GROUP: KHAI TỬ
    // ================================================================

    @Nested
    @DisplayName("Khai tử công dân")
    class KhaiTuTests {

        @Test
        @DisplayName("Khai tử thành công, đổi trạng thái sang DA_CHET")
        void khaiTu_success_changesStatusToKhaiTu() {
            // Arrange
            when(congDanRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
            when(congDanRepository.save(any())).thenReturn(savedEntity);
            doNothing().when(eventPublisher).publishCongDanKhaiTu(any());

            // Act
            congDanService.khaiTu(1L, "Bệnh lý");

            // Assert
            assertThat(savedEntity.getTrangThai()).isEqualTo("DA_CHET");
            assertThat(savedEntity.getNgayKhaiTu()).isEqualTo(LocalDate.now());
            assertThat(savedEntity.getLyDoTrangThai()).isEqualTo("Bệnh lý");

            verify(congDanRepository).save(savedEntity);
            verify(eventPublisher).publishCongDanKhaiTu(savedEntity);
        }

        @Test
        @DisplayName("Ném exception khi không tìm thấy công dân để khai tử")
        void khaiTu_throwsNotFound_whenCongDanNotExist() {
            when(congDanRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> congDanService.khaiTu(999L, "Lý do"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(congDanRepository, never()).save(any());
        }
    }
}