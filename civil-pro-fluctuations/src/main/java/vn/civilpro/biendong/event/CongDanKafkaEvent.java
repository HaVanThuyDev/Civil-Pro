package vn.civilpro.biendong.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event payload nhận từ CongDan Service.
 * Phải khớp cấu trúc với CongDanEvent bên civil-pro-cong-dan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongDanKafkaEvent {
    private String eventType;
    private Long congDanId;
    private String maCongDan;
    private String hoTen;
    private String ngaySinh;       // ISO date string YYYY-MM-DD
    private String maDvhc;
    private String trangThai;
}