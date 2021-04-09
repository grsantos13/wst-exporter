FROM openjdk:11
ARG JAR_FILE=build/libs/*all.jar
COPY ${JAR_FILE} wst-exporter.jar
ENTRYPOINT ["java","-jar","/wst-exporter.jar"]