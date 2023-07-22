FROM openjdk:11
EXPOSE 8080
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/piley-server.jar
ENTRYPOINT ["java","-jar","/app/piley-server.jar"]