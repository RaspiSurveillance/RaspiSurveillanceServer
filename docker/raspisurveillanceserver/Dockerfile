FROM gradle:7.4.1-jdk17 AS build

COPY build.gradle /build/
COPY src /build/src/

WORKDIR /build/

RUN gradle build --no-daemon

FROM openjdk:17.0.2-jdk-oraclelinux8

WORKDIR /app

COPY --from=build /build/build/libs/*.jar /app/raspisurveillance-server.jar

COPY docker/wait-for-it.sh wait-for-it.sh 
RUN chmod +x wait-for-it.sh

ENTRYPOINT ["./wait-for-it.sh" , "db:3306" , "--strict" , "--timeout=30" , "--" , "java", "-jar","/app/raspisurveillance-server.jar"]