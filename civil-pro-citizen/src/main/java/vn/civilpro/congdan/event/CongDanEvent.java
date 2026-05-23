package vn.civilpro.congdan.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * ================================================================
 * KAFKA EVENT PAYLOAD - CÔNG DÂN
 * Message được serialize thành JSON và gửi lên Kafka topic.
 * Các service subscriber deserialize về class này.
 * ================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongDanEvent {

    /** Loại sự kiện: CONG_DAN_CREATED / CONG_DAN_UPDATED / CONG_DAN_KHAI_TU */
    private String eventType;

    /** ID công dân trong DB_CONG_DAN */
    private Long congDanId;

    /** Mã nội bộ (CD-XXXXXX) */
    private String maCongDan;

    /** Tên đầy đủ (để các service hiển thị mà không cần gọi lại) */
    private String hoTen;

    /** ĐVHC thường trú (để ThongKe Service cập nhật thống kê theo vùng) */
    private String maDvhc;

    /** Trạng thái mới sau sự kiện */
    private String trangThai;

    /** Thời điểm sự kiện xảy ra */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant occurredAt;
}