-- ==============================================================================
-- CIVIL-PRO PAYMENT SERVICE (civilpro.pay) - DATABASE SCHEMA & SEED DATA (MySQL)
-- Chuẩn kiến trúc giao dịch Ngân hàng & Thuế Nhà nước Việt Nam
-- Bảng và Cột chuẩn 100% theo các JPA Entity trong vn.civilpro.pay.entity
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `civilpro_pay` 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `civilpro_pay`;

-- Tắt kiểm tra khóa ngoại để khởi tạo sạch sẽ nếu chạy lại
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `PAYMENT_AUDIT_LOG`;
DROP TABLE IF EXISTS `PAYMENT_LEDGER_ENTRY`;
DROP TABLE IF EXISTS `PAYMENT_TRANSACTION`;
DROP TABLE IF EXISTS `PAYMENT_ORDER`;
SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================================
-- 1. BẢNG: PAYMENT_ORDER (Lệnh nộp ngân sách nhà nước / thuế)
-- Map entity: vn.civilpro.pay.entity.PaymentOrder
-- ==============================================================================
CREATE TABLE `PAYMENT_ORDER` (
    `ID`                   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính định danh lệnh nộp thuế',
    `ORDER_CODE`           VARCHAR(50) NOT NULL COMMENT 'Mã lệnh thanh toán duy nhất (VD: ORD-TAX-2026-001)',
    `TAX_CATEGORY`         VARCHAR(50) NOT NULL COMMENT 'Loại thuế: PERSONAL_INCOME_TAX, VALUE_ADDED_TAX, SPECIAL_CONSUMPTION_TAX, NON_AGRI_LAND_TAX, AGRI_LAND_TAX, REAL_ESTATE_TRANSFER_TAX, SECURITIES_TRANSFER_TAX',
    `TAXPAYER_NATIONAL_ID` VARCHAR(30) NOT NULL COMMENT 'Số CCCD/Định danh cá nhân người nộp',
    `TAXPAYER_NAME`        VARCHAR(255) NOT NULL COMMENT 'Họ và tên người nộp thuế',
    `TAX_CODE`             VARCHAR(30) NULL COMMENT 'Mã số thuế cá nhân/hộ kinh doanh',
    `AMOUNT`               DECIMAL(18, 2) NOT NULL COMMENT 'Số tiền thuế phải nộp (VND)',
    `CURRENCY`             VARCHAR(10) NOT NULL DEFAULT 'VND' COMMENT 'Loại tiền tệ (VND)',
    `FISCAL_PERIOD`        VARCHAR(30) NULL COMMENT 'Kỳ tính thuế (VD: Q1/2026, 2026, 03/2026)',
    `STATUS`               VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED',
    `IDEMPOTENCY_KEY`      VARCHAR(100) NULL COMMENT 'Khóa chống thanh toán trùng lặp (Idempotency Key)',
    `RECEIPT_NUMBER`       VARCHAR(100) NULL COMMENT 'Số biên lai điện tử kho bạc (BL-TAX-YYYY-XXXX)',
    `NOTES`                TEXT NULL COMMENT 'Ghi chú / nội dung nộp ngân sách',
    `CREATED_AT`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian lập lệnh',
    `COMPLETED_AT`         DATETIME NULL COMMENT 'Thời gian hoàn tất giao dịch',
    `VERSION`              INT NOT NULL DEFAULT 0 COMMENT 'Khóa lạc quan chống xung đột đồng thời (@Version)',
    
    CONSTRAINT `UQ_PO_ORDER_CODE` UNIQUE (`ORDER_CODE`),
    CONSTRAINT `UQ_PO_IDEMPOTENCY` UNIQUE (`IDEMPOTENCY_KEY`),
    INDEX `IDX_PO_TAXPAYER` (`TAXPAYER_NATIONAL_ID`),
    INDEX `IDX_PO_STATUS` (`STATUS`),
    INDEX `IDX_PO_TAX_CAT` (`TAX_CATEGORY`),
    INDEX `IDX_PO_CREATED` (`CREATED_AT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng quản lý các lệnh nộp thuế nhà nước';

-- ==============================================================================
-- 2. BẢNG: PAYMENT_TRANSACTION (Giao dịch thanh toán ngân hàng / cổng thanh toán)
-- Map entity: vn.civilpro.pay.entity.PaymentTransaction
-- ==============================================================================
CREATE TABLE `PAYMENT_TRANSACTION` (
    `ID`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính giao dịch',
    `TXN_REFERENCE`   VARCHAR(64) NOT NULL COMMENT 'Mã tham chiếu ngân hàng duy nhất (TXN-...)',
    `ORDER_ID`        BIGINT NOT NULL COMMENT 'Khóa ngoại tham chiếu sang PAYMENT_ORDER.ID',
    `PAYMENT_METHOD`  VARCHAR(50) NOT NULL COMMENT 'Phương thức: BANK_TRANSFER, VNPAY, STATE_TREASURY_DIRECT, DIGITAL_WALLET',
    `DEBIT_ACCOUNT`   VARCHAR(64) NOT NULL COMMENT 'Tài khoản nguồn trích nợ của người nộp',
    `CREDIT_ACCOUNT`  VARCHAR(64) NOT NULL DEFAULT 'VN-STATE-TREASURY-8888' COMMENT 'Tài khoản thụ hưởng Kho bạc Nhà nước',
    `AMOUNT`          DECIMAL(18, 2) NOT NULL COMMENT 'Số tiền thực hiện giao dịch',
    `FEE`             DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT 'Phí giao dịch thanh toán (0 VND cho dịch vụ công)',
    `CHECKSUM`        VARCHAR(128) NULL COMMENT 'Mã băm toàn vẹn SHA-256 xác thực giao dịch',
    `STATUS`          VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING, SUCCESS, FAILED, REVERSED',
    `GATEWAY_TXN_ID`  VARCHAR(100) NULL COMMENT 'Mã giao dịch từ Gateway / Ngân hàng liên kết',
    `FAILURE_REASON`  TEXT NULL COMMENT 'Lý do thất bại nếu có',
    `CREATED_AT`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian khởi tạo thanh toán',
    `COMPLETED_AT`    DATETIME NULL COMMENT 'Thời gian ngân hàng xác nhận thành công',
    `VERSION`         INT NOT NULL DEFAULT 0 COMMENT 'Khóa lạc quan (@Version)',

    CONSTRAINT `UQ_PT_TXN_REF` UNIQUE (`TXN_REFERENCE`),
    CONSTRAINT `FK_PT_ORDER` FOREIGN KEY (`ORDER_ID`) REFERENCES `PAYMENT_ORDER` (`ID`) ON DELETE CASCADE,
    INDEX `IDX_PT_ORDER_ID` (`ORDER_ID`),
    INDEX `IDX_PT_STATUS` (`STATUS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng giao dịch thanh toán ngân hàng';

-- ==============================================================================
-- 3. BẢNG: PAYMENT_LEDGER_ENTRY (Sổ cái kế toán kép chuẩn ngân hàng - Double-Entry)
-- Map entity: vn.civilpro.pay.entity.LedgerEntry
-- Bất biến: Tổng tiền DEBIT (Nợ) luôn bằng Tổng tiền CREDIT (Có)
-- ==============================================================================
CREATE TABLE `PAYMENT_LEDGER_ENTRY` (
    `ID`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính bút toán',
    `TRANSACTION_ID`  BIGINT NOT NULL COMMENT 'Khóa tham chiếu giao dịch PAYMENT_TRANSACTION.ID',
    `ENTRY_TYPE`      VARCHAR(20) NOT NULL COMMENT 'Loại bút toán: DEBIT (Ghi nợ) hoặc CREDIT (Ghi có)',
    `ACCOUNT_NUMBER`  VARCHAR(64) NOT NULL COMMENT 'Số tài khoản ghi nhận',
    `ACCOUNT_NAME`    VARCHAR(255) NOT NULL COMMENT 'Tên chủ tài khoản',
    `AMOUNT`          DECIMAL(18, 2) NOT NULL COMMENT 'Số tiền phát sinh của bút toán',
    `BALANCE_AFTER`   DECIMAL(18, 2) NULL COMMENT 'Số dư khả dụng sau khi hạch toán',
    `DESCRIPTION`     VARCHAR(500) NULL COMMENT 'Diễn giải hạch toán kế toán',
    `POSTED_AT`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm ghi sổ kế toán',

    INDEX `IDX_LE_TXN_ID` (`TRANSACTION_ID`),
    INDEX `IDX_LE_ACC` (`ACCOUNT_NUMBER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Sổ cái kế toán kép chuẩn ứng dụng ngân hàng';

-- ==============================================================================
-- 4. BẢNG: PAYMENT_AUDIT_LOG (Nhật ký kiểm toán an ninh và truy vết tài chính)
-- Map entity: vn.civilpro.pay.entity.PaymentAuditLog
-- ==============================================================================
CREATE TABLE `PAYMENT_AUDIT_LOG` (
    `ID`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Khóa chính log',
    `ENTITY_TYPE`     VARCHAR(50) NOT NULL COMMENT 'Tên thực thể (VD: PaymentOrder, PaymentTransaction)',
    `ENTITY_ID`       BIGINT NOT NULL COMMENT 'ID của thực thể chịu tác động',
    `ACTION`          VARCHAR(50) NOT NULL COMMENT 'Hành động: CREATE_ORDER, PROCESS_PAYMENT, PAYMENT_COMPLETED, REVERSE_PAYMENT',
    `PREVIOUS_STATE`  VARCHAR(50) NULL COMMENT 'Trạng thái trước khi tác động',
    `NEW_STATE`       VARCHAR(50) NULL COMMENT 'Trạng thái sau khi tác động',
    `PERFORMED_BY`    VARCHAR(100) NULL COMMENT 'Định danh người/hệ thống thực hiện (Username/CCCD)',
    `CLIENT_IP`       VARCHAR(50) NULL COMMENT 'Địa chỉ IP của client gọi API',
    `OCCURRED_AT`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm phát sinh sự kiện',

    INDEX `IDX_PAL_ENTITY` (`ENTITY_TYPE`, `ENTITY_ID`),
    INDEX `IDX_PAL_TIME` (`OCCURRED_AT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Nhật ký kiểm toán tài chính';

-- ==============================================================================
-- DỮ LIỆU MẪU ĐẦY ĐỦ (SEED DATA)
-- Bao gồm cả 7 loại thuế, đầy đủ Order, Transaction, Sổ kép Ledger & Audit Log
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- DỮ LIỆU BẢNG PAYMENT_ORDER
-- ------------------------------------------------------------------------------
INSERT INTO `PAYMENT_ORDER` (
    `ID`, `ORDER_CODE`, `TAX_CATEGORY`, `TAXPAYER_NATIONAL_ID`, `TAXPAYER_NAME`, 
    `TAX_CODE`, `AMOUNT`, `CURRENCY`, `FISCAL_PERIOD`, `STATUS`, 
    `IDEMPOTENCY_KEY`, `RECEIPT_NUMBER`, `NOTES`, `CREATED_AT`, `COMPLETED_AT`, `VERSION`
) VALUES
-- 1. Thuế thu nhập cá nhân (TNCN) - COMPLETED
(1, 'ORD-TAX-2026-0001', 'PERSONAL_INCOME_TAX', '001200000001', 'Nguyễn Văn An', 
 '8839210492', 5500000.00, 'VND', 'Q1/2026', 'COMPLETED', 
 'IDEMP-2026-TNCN-001', 'BL-TAX-2026-00000001', 'Quyết toán thuế thu nhập cá nhân quý 1/2026 từ tiền lương', 
 '2026-03-15 08:30:00', '2026-03-15 08:31:12', 1),

-- 2. Thuế Giá trị gia tăng (GTGT) - COMPLETED
(2, 'ORD-TAX-2026-0002', 'VALUE_ADDED_TAX', '001200000002', 'Trần Thị Bích', 
 '8541209381', 3200000.00, 'VND', '03/2026', 'COMPLETED', 
 'IDEMP-2026-VAT-002', 'BL-TAX-2026-00000002', 'Nộp thuế GTGT hoạt động bán hàng thương mại điện tử tháng 3/2026', 
 '2026-03-16 09:15:00', '2026-03-16 09:16:05', 1),

-- 3. Thuế Tiêu thụ đặc biệt - COMPLETED
(3, 'ORD-TAX-2026-0003', 'SPECIAL_CONSUMPTION_TAX', '001200000003', 'Lê Hoàng Cường', 
 '8619203841', 45000000.00, 'VND', '2026', 'COMPLETED', 
 'IDEMP-2026-SCT-003', 'BL-TAX-2026-00000003', 'Thuế tiêu thụ đặc biệt nhập khẩu ô tô chở người dưới 9 chỗ', 
 '2026-03-17 10:00:00', '2026-03-17 10:02:40', 1),

-- 4. Thuế Sử dụng đất phi nông nghiệp - PENDING (Chờ thanh toán)
(4, 'ORD-TAX-2026-0004', 'NON_AGRI_LAND_TAX', '001200000004', 'Phạm Minh Đức', 
 '8729104829', 680000.00, 'VND', '2026', 'PENDING', 
 NULL, NULL, 'Thuế đất phi nông nghiệp thửa số 15, tờ bản đồ 04, P. Cầu Giấy, Hà Nội', 
 '2026-03-18 14:00:00', NULL, 0),

-- 5. Thuế Sử dụng đất nông nghiệp - PENDING (Chờ thanh toán)
(5, 'ORD-TAX-2026-0005', 'AGRI_LAND_TAX', '001200000005', 'Hoàng Thị Mai', 
 '8491028472', 250000.00, 'VND', '2026', 'PENDING', 
 NULL, NULL, 'Thuế sử dụng đất nông nghiệp trồng cây hàng năm xã Tân Lập', 
 '2026-03-19 11:30:00', NULL, 0),

-- 6. Thuế Chuyển nhượng Bất động sản - COMPLETED
(6, 'ORD-TAX-2026-0006', 'REAL_ESTATE_TRANSFER_TAX', '001200000006', 'Vũ Tuấn Anh', 
 '8839210492', 52000000.00, 'VND', '2026', 'COMPLETED', 
 'IDEMP-2026-RET-006', 'BL-TAX-2026-00000006', 'Thuế thu nhập chuyển nhượng căn hộ chung cư Ecolife Capitol HĐ số CC-2026/18', 
 '2026-03-19 15:20:00', '2026-03-19 15:22:15', 1),

-- 7. Thuế Chuyển nhượng Chứng khoán - COMPLETED
(7, 'ORD-TAX-2026-0007', 'SECURITIES_TRANSFER_TAX', '001200000007', 'Đặng Quốc Huy', 
 '8910283719', 1850000.00, 'VND', '03/2026', 'COMPLETED', 
 'IDEMP-2026-STT-007', 'BL-TAX-2026-00000007', 'Thuế 0.1% từ chuyển nhượng chứng khoán niêm yết sàn HoSE tháng 3/2026', 
 '2026-03-20 08:45:00', '2026-03-20 08:46:20', 1),

-- 8. Lệnh đang trong tiến trình xử lý - PROCESSING (Kiểm tra chống xung đột đồng thời)
(8, 'ORD-TAX-2026-0008', 'PERSONAL_INCOME_TAX', '001200000008', 'Ngô Bảo Châu', 
 '8831092834', 12000000.00, 'VND', 'Q1/2026', 'PROCESSING', 
 'IDEMP-2026-INFLIGHT-008', NULL, 'Đang xác thực trừ tiền từ ngân hàng liên kết', 
 '2026-03-20 09:00:00', NULL, 0),

-- 9. Lệnh thanh toán thất bại - FAILED (Tài khoản không đủ số dư)
(9, 'ORD-TAX-2026-0009', 'NON_AGRI_LAND_TAX', '001200000009', 'Bùi Văn Hưng', 
 '8719283746', 950000.00, 'VND', '2026', 'FAILED', 
 'IDEMP-2026-FAILED-009', NULL, 'Thất bại do số dư tài khoản nguồn không đủ', 
 '2026-03-20 09:30:00', '2026-03-20 09:31:00', 1);

-- ------------------------------------------------------------------------------
-- DỮ LIỆU BẢNG PAYMENT_TRANSACTION
-- ------------------------------------------------------------------------------
INSERT INTO `PAYMENT_TRANSACTION` (
    `ID`, `TXN_REFERENCE`, `ORDER_ID`, `PAYMENT_METHOD`, `DEBIT_ACCOUNT`, 
    `CREDIT_ACCOUNT`, `AMOUNT`, `FEE`, `CHECKSUM`, `STATUS`, 
    `GATEWAY_TXN_ID`, `FAILURE_REASON`, `CREATED_AT`, `COMPLETED_AT`, `VERSION`
) VALUES
-- Transaction cho Order 1 (TNCN)
(1, 'TXN-1789849001-A1B2C3D4', 1, 'BANK_TRANSFER', '19038291028301', 
 'VN-STATE-TREASURY-8888', 5500000.00, 0.00, 'a7c9f28d8b9e6a5c3e7f1b2c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e', 'SUCCESS', 
 'GW-NAPAS-8819201', NULL, '2026-03-15 08:30:10', '2026-03-15 08:31:12', 0),

-- Transaction cho Order 2 (GTGT)
(2, 'TXN-1789849002-E5F6G7H8', 2, 'VNPAY', '9704198273619283', 
 'VN-STATE-TREASURY-8888', 3200000.00, 0.00, 'b8d0a39e9c0f7b6d4f8a2c3d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f', 'SUCCESS', 
 'GW-VNPAY-7729103', NULL, '2026-03-16 09:15:15', '2026-03-16 09:16:05', 0),

-- Transaction cho Order 3 (Tiêu thụ đặc biệt)
(3, 'TXN-1789849003-I9J0K1L2', 3, 'STATE_TREASURY_DIRECT', '0491000192837', 
 'VN-STATE-TREASURY-8888', 45000000.00, 0.00, 'c9e1b40f0d1a8c7e5a9b3d4e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a', 'SUCCESS', 
 'GW-TREASURY-992810', NULL, '2026-03-17 10:00:20', '2026-03-17 10:02:40', 0),

-- Transaction cho Order 6 (Chuyển nhượng BĐS)
(4, 'TXN-1789849006-M3N4O5P6', 6, 'BANK_TRANSFER', '102938475610', 
 'VN-STATE-TREASURY-8888', 52000000.00, 0.00, 'd0f2c51a1e2b9d8f6b0c4e5f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b', 'SUCCESS', 
 'GW-NAPAS-9920194', NULL, '2026-03-19 15:20:10', '2026-03-19 15:22:15', 0),

-- Transaction cho Order 7 (Chứng khoán)
(5, 'TXN-1789849007-Q7R8S9T0', 7, 'DIGITAL_WALLET', '0912345678', 
 'VN-STATE-TREASURY-8888', 1850000.00, 0.00, 'e1a3d62b2f3c0e9a7c1d5f6a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c', 'SUCCESS', 
 'GW-MOMO-3392018', NULL, '2026-03-20 08:45:15', '2026-03-20 08:46:20', 0),

-- Transaction cho Order 9 (Thất bại)
(6, 'TXN-1789849009-U1V2W3X4', 9, 'BANK_TRANSFER', '19028374659102', 
 'VN-STATE-TREASURY-8888', 950000.00, 0.00, 'f2b4e73c3a4d1f0b8d2e6a7b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d', 'FAILED', 
 'GW-NAPAS-ERROR-51', 'Tài khoản trích nợ không đủ số dư khả dụng (Error Code 51)', '2026-03-20 09:30:10', '2026-03-20 09:31:00', 0);

-- ------------------------------------------------------------------------------
-- DỮ LIỆU BẢNG PAYMENT_LEDGER_ENTRY (Hạch toán kế toán kép chuẩn Ngân hàng)
-- Invariant: Mỗi Transaction thành công sinh ra đúng 1 cặp DEBIT và CREDIT khớp số tiền
-- ------------------------------------------------------------------------------
INSERT INTO `PAYMENT_LEDGER_ENTRY` (
    `ID`, `TRANSACTION_ID`, `ENTRY_TYPE`, `ACCOUNT_NUMBER`, `ACCOUNT_NAME`, 
    `AMOUNT`, `BALANCE_AFTER`, `DESCRIPTION`, `POSTED_AT`
) VALUES
-- Cặp bút toán Transaction 1: Thuế TNCN (5,500,000 VND)
(1, 1, 'DEBIT',  '19038291028301',        'Nguyễn Văn An', 
 5500000.00, 44500000.00, 'Trích nợ tài khoản nộp thuế TNCN kỳ Q1/2026 (Lệnh: ORD-TAX-2026-0001)', '2026-03-15 08:31:12'),
(2, 1, 'CREDIT', 'VN-STATE-TREASURY-8888', 'Kho bạc Nhà nước Việt Nam', 
 5500000.00, 100005500000.00, 'Ghi có thu ngân sách nhà nước: Thuế TNCN từ Nguyễn Văn An', '2026-03-15 08:31:12'),

-- Cặp bút toán Transaction 2: Thuế GTGT (3,200,000 VND)
(3, 2, 'DEBIT',  '9704198273619283',       'Trần Thị Bích', 
 3200000.00, 28800000.00, 'Trích nợ tài khoản nộp thuế GTGT tháng 03/2026 (Lệnh: ORD-TAX-2026-0002)', '2026-03-16 09:16:05'),
(4, 2, 'CREDIT', 'VN-STATE-TREASURY-8888', 'Kho bạc Nhà nước Việt Nam', 
 3200000.00, 100008700000.00, 'Ghi có thu ngân sách nhà nước: Thuế GTGT từ Trần Thị Bích', '2026-03-16 09:16:05'),

-- Cặp bút toán Transaction 3: Thuế Tiêu thụ đặc biệt (45,000,000 VND)
(5, 3, 'DEBIT',  '0491000192837',          'Lê Hoàng Cường', 
 45000000.00, 155000000.00, 'Trích nợ tài khoản nộp thuế TTĐB nhập khẩu ô tô (Lệnh: ORD-TAX-2026-0003)', '2026-03-17 10:02:40'),
(6, 3, 'CREDIT', 'VN-STATE-TREASURY-8888', 'Kho bạc Nhà nước Việt Nam', 
 45000000.00, 100053700000.00, 'Ghi có thu ngân sách nhà nước: Thuế TTĐB từ Lê Hoàng Cường', '2026-03-17 10:02:40'),

-- Cặp bút toán Transaction 4: Thuế Chuyển nhượng BĐS (52,000,000 VND)
(7, 4, 'DEBIT',  '102938475610',           'Vũ Tuấn Anh', 
 52000000.00, 248000000.00, 'Trích nợ tài khoản nộp thuế chuyển nhượng BĐS (Lệnh: ORD-TAX-2026-0006)', '2026-03-19 15:22:15'),
(8, 4, 'CREDIT', 'VN-STATE-TREASURY-8888', 'Kho bạc Nhà nước Việt Nam', 
 52000000.00, 100105700000.00, 'Ghi có thu ngân sách nhà nước: Thuế chuyển nhượng BĐS từ Vũ Tuấn Anh', '2026-03-19 15:22:15'),

-- Cặp bút toán Transaction 5: Thuế Chuyển nhượng Chứng khoán (1,850,000 VND)
(9, 5, 'DEBIT',  '0912345678',             'Đặng Quốc Huy', 
 1850000.00, 18150000.00, 'Trích nợ ví điện tử nộp thuế chứng khoán sàn HoSE (Lệnh: ORD-TAX-2026-0007)', '2026-03-20 08:46:20'),
(10, 5, 'CREDIT', 'VN-STATE-TREASURY-8888', 'Kho bạc Nhà nước Việt Nam', 
 1850000.00, 100107550000.00, 'Ghi có thu ngân sách nhà nước: Thuế chứng khoán từ Đặng Quốc Huy', '2026-03-20 08:46:20');

-- ------------------------------------------------------------------------------
-- DỮ LIỆU BẢNG PAYMENT_AUDIT_LOG (Nhật ký truy vết an ninh & kiểm toán)
-- ------------------------------------------------------------------------------
INSERT INTO `PAYMENT_AUDIT_LOG` (
    `ID`, `ENTITY_TYPE`, `ENTITY_ID`, `ACTION`, `PREVIOUS_STATE`, `NEW_STATE`, 
    `PERFORMED_BY`, `CLIENT_IP`, `OCCURRED_AT`
) VALUES
-- Audit logs cho Order 1
(1, 'PaymentOrder', 1, 'CREATE_ORDER', NULL, 'PENDING', '001200000001', '192.168.1.10', '2026-03-15 08:30:00'),
(2, 'PaymentOrder', 1, 'PROCESS_PAYMENT', 'PENDING', 'PROCESSING', 'nguyenvanan', '192.168.1.10', '2026-03-15 08:30:10'),
(3, 'PaymentOrder', 1, 'PAYMENT_COMPLETED', 'PROCESSING', 'COMPLETED', 'nguyenvanan', '192.168.1.10', '2026-03-15 08:31:12'),

-- Audit logs cho Order 2
(4, 'PaymentOrder', 2, 'CREATE_ORDER', NULL, 'PENDING', '001200000002', '192.168.1.15', '2026-03-16 09:15:00'),
(5, 'PaymentOrder', 2, 'PAYMENT_COMPLETED', 'PENDING', 'COMPLETED', 'tranthibich', '192.168.1.15', '2026-03-16 09:16:05'),

-- Audit logs cho Order 3
(6, 'PaymentOrder', 3, 'CREATE_ORDER', NULL, 'PENDING', '001200000003', '192.168.1.20', '2026-03-17 10:00:00'),
(7, 'PaymentOrder', 3, 'PAYMENT_COMPLETED', 'PENDING', 'COMPLETED', 'lehoangcuong', '192.168.1.20', '2026-03-17 10:02:40'),

-- Audit logs cho Order 4 & 5 (Tạo lệnh chờ nộp)
(8, 'PaymentOrder', 4, 'CREATE_ORDER', NULL, 'PENDING', '001200000004', '192.168.1.25', '2026-03-18 14:00:00'),
(9, 'PaymentOrder', 5, 'CREATE_ORDER', NULL, 'PENDING', '001200000005', '192.168.1.30', '2026-03-19 11:30:00'),

-- Audit logs cho Order 6 & 7
(10, 'PaymentOrder', 6, 'CREATE_ORDER', NULL, 'PENDING', '001200000006', '192.168.1.35', '2026-03-19 15:20:00'),
(11, 'PaymentOrder', 6, 'PAYMENT_COMPLETED', 'PENDING', 'COMPLETED', 'vutuananh', '192.168.1.35', '2026-03-19 15:22:15'),
(12, 'PaymentOrder', 7, 'CREATE_ORDER', NULL, 'PENDING', '001200000007', '192.168.1.40', '2026-03-20 08:45:00'),
(13, 'PaymentOrder', 7, 'PAYMENT_COMPLETED', 'PENDING', 'COMPLETED', 'dangquochuy', '192.168.1.40', '2026-03-20 08:46:20'),

-- Audit log cho Order 9 (Thất bại)
(14, 'PaymentOrder', 9, 'CREATE_ORDER', NULL, 'PENDING', '001200000009', '192.168.1.45', '2026-03-20 09:30:00'),
(15, 'PaymentOrder', 9, 'PAYMENT_FAILED', 'PENDING', 'FAILED', 'buivanhung', '192.168.1.45', '2026-03-20 09:31:00');

-- ==============================================================================
-- HOÀN TẤT KHỞI TẠO DỮ LIỆU
-- ==============================================================================
COMMIT;
