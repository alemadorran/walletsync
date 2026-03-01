#!/bin/bash

# Colores para la terminal
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}==> Deteniendo contenedores previos...${NC}"
docker-compose down

echo -e "${GREEN}==> Construyendo y levantando el proyecto (walletsync)...${NC}"
# --build fuerza a que se reconstruya el JAR si has cambiado el código
# -d lo ejecuta en segundo plano
docker-compose up --build -d

echo -e "${GREEN}==> ¡Listo! La aplicación está arrancando.${NC}"
echo -e "${GREEN}==> Mostrando logs (Ctrl+C para salir de los logs, el contenedor seguirá corriendo):${NC}"

# Seguir los logs del contenedor de la app para ver si Spring arranca bien
docker-compose logs -f app