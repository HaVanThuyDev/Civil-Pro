-- ====================================================================
-- CIVIL-PRO: Tự động khởi tạo Database cho tất cả các Microservices
-- Tự động thực thi khi container civil-mysql khởi động lần đầu
-- ====================================================================

-- 1. Database cho civil-pro-auth
CREATE DATABASE IF NOT EXISTS DB_AUTH 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 2. Database cho civil-pro-citizen (Công dân & CCCD)
CREATE DATABASE IF NOT EXISTS DB_CONG_DAN 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 3. Database cho civil-pro-household (Sổ Hộ khẩu)
CREATE DATABASE IF NOT EXISTS DB_HOUSEHOLD 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 4. Database cho civil-pro-fluctuations (Biến động dân cư)
CREATE DATABASE IF NOT EXISTS DB_FLUCTUATIONS 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 5. Database cho civil-pro-statistical (Báo cáo thống kê)
CREATE DATABASE IF NOT EXISTS DB_STATISTICAL 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 6. Database cho civil-pro-synchronized (Đồng bộ CSDL Quốc gia)
CREATE DATABASE IF NOT EXISTS DB_SYNCHRONIZED 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 7. Database cho civil-pro-pay (Thanh toán & Thu nộp thuế)
CREATE DATABASE IF NOT EXISTS DB_PAYMENT 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Cấp toàn quyền cho root truy cập từ mọi container trong mạng Docker
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '11032003' WITH GRANT OPTION;
FLUSH PRIVILEGES;
