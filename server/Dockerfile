# Build context must be the repository root.

# ===== Stage 1: Build admin web UI =====
FROM node:24-alpine AS admin-builder
WORKDIR /build/admin
COPY admin/package.json admin/package-lock.json ./
RUN npm ci
COPY admin/ ./
RUN npm run build

# ===== Stage 2: Build Spring Boot service =====
FROM maven:3.9-eclipse-temurin-17 AS server-builder
WORKDIR /build/server
COPY server/pom.xml ./
RUN mvn dependency:go-offline -B
COPY server/src ./src
COPY --from=admin-builder /build/admin/dist ./src/main/resources/static/console
RUN mvn clean package -DskipTests -B

# ===== Stage 3: Runtime =====
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=server-builder /build/server/target/huarenzaimeng-server.jar app.jar
COPY server/start.sh start.sh
RUN chmod +x start.sh
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1
ENTRYPOINT ["sh", "start.sh"]
