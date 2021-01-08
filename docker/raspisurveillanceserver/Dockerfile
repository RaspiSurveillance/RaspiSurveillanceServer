FROM gradle:6.6.1-jdk8 AS build

COPY build.gradle /build/
COPY src /build/src/

WORKDIR /build/

RUN gradle build --no-daemon

FROM adoptopenjdk/openjdk8:latest

WORKDIR /app

COPY --from=build /build/build/libs/*.jar /app/raspisurveillance-server.jar

COPY docker/wait-for-it.sh wait-for-it.sh 
RUN chmod +x wait-for-it.sh

ENTRYPOINT ["./wait-for-it.sh" , "db:3306" , "--strict" , "--timeout=30" , "--" , "java", "-jar","/app/raspisurveillance-server.jar"]