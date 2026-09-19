#!/bin/bash
set -e

echo "=========================================================="
echo "  CIVIL-PRO: TIẾN TRÌNH BUILD VÀ KHỞI CHẠY DOCKER COMPOSE  "
echo "=========================================================="

echo -e "\n[1/3] Đang biên dịch và đóng gói 12 module Maven..."
mvn clean package -DskipTests

echo -e "\n[2/3] Đang build Docker Images và khởi chạy toàn bộ containers..."
docker compose up --build -d

echo -e "\n[3/3] Trạng thái các container đang hoạt động:"
docker compose ps

echo -e "\n=========================================================="
echo "  HỆ THỐNG CIVIL-PRO ĐÃ KHỞI CHẠY THÀNH CÔNG!"
echo "  - API Gateway:    http://localhost:8000"
echo "  - Eureka Server:  http://localhost:8761"
echo "  - Config Server:  http://localhost:8888"
echo "=========================================================="
echo "Xem logs tổng hợp: docker compose logs -f"
