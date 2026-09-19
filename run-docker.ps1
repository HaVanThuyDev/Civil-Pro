# ====================================================================
# CIVIL-PRO: KỊCH BẢN TỰ ĐỘNG BUILD & KHỞI CHẠY CONTAINER DOCKER
# ====================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  CIVIL-PRO: TIẾN TRÌNH BUILD VÀ KHỞI CHẠY DOCKER COMPOSE  " -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Đóng gói mã nguồn Java thành các tệp thực thi .jar
Write-Host "`n[1/3] Đang biên dịch và đóng gói 12 module Maven..." -ForegroundColor Green
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[LỖI] Quá trình đóng gói Maven thất bại! Vui lòng kiểm tra lỗi code trước khi build Docker." -ForegroundColor Red
    exit $LASTEXITCODE
}

# 2. Build Docker images và khởi chạy container
Write-Host "`n[2/3] Đang build Docker Images và khởi chạy toàn bộ containers..." -ForegroundColor Green
docker compose up --build -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[LỖI] Khởi chạy Docker Compose thất bại!" -ForegroundColor Red
    exit $LASTEXITCODE
}

# 3. Hiển thị trạng thái các container
Write-Host "`n[3/3] Trạng thái các container đang hoạt động:" -ForegroundColor Green
docker compose ps

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "  HỆ THỐNG CIVIL-PRO ĐÃ KHỞI CHẠY THÀNH CÔNG!" -ForegroundColor Green
Write-Host "  - API Gateway:    http://localhost:8000" -ForegroundColor White
Write-Host "  - Eureka Server:  http://localhost:8761" -ForegroundColor White
Write-Host "  - Config Server:  http://localhost:8888" -ForegroundColor White
Write-Host "  - MySQL:          localhost:3306 (user: root / pass: 11032003)" -ForegroundColor White
Write-Host "  - Redis:          localhost:6379" -ForegroundColor White
Write-Host "  - Kafka:          localhost:9092" -ForegroundColor White
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Xem logs tổng hợp: docker compose logs -f" -ForegroundColor Yellow
