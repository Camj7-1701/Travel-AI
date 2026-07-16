FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json .
COPY frontend/package-lock.json .
RUN npm install --prefer-offline --no-audit
COPY frontend .
RUN npm run build

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN mkdir -p data uploads
COPY --from=backend-build /app/backend/target/travel-ai.jar ./travel-ai.jar
COPY --from=frontend-build /app/frontend/dist ./static
RUN ls -la travel-ai.jar || echo "jar not found"
EXPOSE 8080
CMD ["java", "-jar", "travel-ai.jar"]
