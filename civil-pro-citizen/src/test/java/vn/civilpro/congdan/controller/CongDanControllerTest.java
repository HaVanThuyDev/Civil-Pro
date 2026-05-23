package vn.civilpro.congdan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.civilpro.congdan.dto.request.CreateCongDanRequest;
import vn.civilpro.congdan.dto.response.CongDanDetailResponse;
import vn.civilpro.congdan.service.CongDanService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ================================================================
 * INTEGRATION TEST: CONG DAN CONTROLLER
 * @WebMvcTest: chỉ load Controller layer, mock Service layer
 * ================================================================
 */
@WebMvcTest(CongDanController.class)
@DisplayName("CongDanController Integration Tests")
class CongDanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CongDanService congDanService;

    // ---- Test fixtures ----

    private CreateCongDanRequest validCreateRequest() {
        CreateCongDanRequest req = new CreateCongDanRequest();
        req.setHoTen("Nguyễn Văn Test");
        req.setGioiTinh(1);
        req.setNgaySinh(LocalDate.of(1990, 1, 1));
        req.setSoCccd("038090099999");
        req.setMaDvhcThuongTru("TP-002");
        req.setDiaChiThuongTru("123 Đường Test");
        return req;
    }

    // ================================================================
    // POST /cong-dan
    // ================================================================

    @Test
    @WithMockUser(authorities = "CONG_DAN:CREATE")
    @DisplayName("POST /cong-dan - Tạo thành công trả về 201")
    void createCongDan_returns201_whenSuccess() throws Exception {
        CongDanDetailResponse mockResponse = CongDanDetailResponse.builder()
                .id(1L)
                .maCongDan("CD-100001")
                .hoTen("Nguyễn Văn Test")
                .trangThai("HOAT_DONG")
                .build();

        when(congDanService.create(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/cong-dan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.maCongDan").value("CD-100001"))
                .andExpect(jsonPath("$.data.hoTen").value("Nguyễn Văn Test"));
    }

    @Test
    @WithMockUser(authorities = "CONG_DAN:CREATE")
    @DisplayName("POST /cong-dan - Trả về 400 khi thiếu hoTen")
    void createCongDan_returns400_whenHoTenBlank() throws Exception {
        CreateCongDanRequest invalidReq = validCreateRequest();
        invalidReq.setHoTen("");

        mockMvc.perform(post("/cong-dan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.hoTen").exists());
    }

    @Test
    @WithMockUser(authorities = "CONG_DAN:CREATE")
    @DisplayName("POST /cong-dan - Trả về 400 khi CCCD sai format")
    void createCongDan_returns400_whenCccdInvalid() throws Exception {
        CreateCongDanRequest invalidReq = validCreateRequest();
        invalidReq.setSoCccd("12345");   // Không đủ 12 số

        mockMvc.perform(post("/cong-dan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /cong-dan - Trả về 401 khi không có token")
    void createCongDan_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post("/cong-dan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "CONG_DAN:READ")  // Sai quyền
    @DisplayName("POST /cong-dan - Trả về 403 khi thiếu quyền CREATE")
    void createCongDan_returns403_whenMissingPermission() throws Exception {
        mockMvc.perform(post("/cong-dan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest())))
                .andExpect(status().isForbidden());
    }

    // ================================================================
    // GET /cong-dan/{id}
    // ================================================================

    @Test
    @WithMockUser(authorities = "CONG_DAN:READ")
    @DisplayName("GET /cong-dan/{id} - Trả về 200 khi tìm thấy")
    void getById_returns200_whenFound() throws Exception {
        CongDanDetailResponse mockResponse = CongDanDetailResponse.builder()
                .id(1L)
                .maCongDan("CD-100001")
                .hoTen("Nguyễn Văn Test")
                .build();

        when(congDanService.getById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/cong-dan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.hoTen").value("Nguyễn Văn Test"));
    }
}