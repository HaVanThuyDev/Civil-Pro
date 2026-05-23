@echo off
chcp 65001 > nul
echo =======================================================
echo     BAT DAU QUY TRINH DONG GOI VA TRIEN KHAI K8S
echo =======================================================
echo.
echo [1/3] Dang tao Docker Image...
docker build -t havanthuydev/civil-pro:v1 .
if %errorlevel% neq 0 (echo [LOI] Build Image that bai! & pause & exit /b)

echo.
echo [2/3] Dang day Image len Docker Hub...
docker push havanthuydev/civil-pro:v1
if %errorlevel% neq 0 (echo [LOI] Push Image that bai! & pause & exit /b)

echo.
echo [3/3] Dang nap cau hinh vao Kubernetes...
kubectl apply -f k8s.yaml
if %errorlevel% neq 0 (echo [LOI] Trien khai K8s that bai! & pause & exit /b)

echo.
echo Quy trinh hoan tat! Kiem tra trang thai Pods:
timeout /t 3 > nul
kubectl get pods
pause
