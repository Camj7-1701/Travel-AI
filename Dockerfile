FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

FROM node:18 AS frontend-build
WORKDIR /app
COPY frontend/package.json .
COPY frontend/package-lock.json .
RUN npm install
COPY frontend .
RUN npm run build

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN mkdir -p data
COPY --from=backend-build /app/target/travel-ai-1.0.0.jar .
COPY --from=frontend-build /app/dist ./static
EXPOSE 8080
CMD ["java", "-jar", "travel-ai-1.0.0.jar"]
