Write-Host "==> Deteniendo contenedores previos..." -ForegroundColor Cyan
docker-compose down

Write-Host "==> Construyendo y levantando el proyecto (walletsync)..." -ForegroundColor Cyan
# --build reconstruye la imagen si cambiaste el código Java
docker-compose up --build -d

Write-Host "==> ¡Listo! La aplicación está arrancando." -ForegroundColor Green
Write-Host "==> Mostrando logs (Presiona Ctrl+C para salir de los logs):" -ForegroundColor Yellow

# Seguir los logs
docker-compose logs -f app