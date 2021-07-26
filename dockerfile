FROM openjdk:11.0.7-slim
LABEL maintainer="francoperino@hotmail.com.ar"
ARG JAR_FILE
ADD target/${JAR_FILE} dan-ms-pedidos-0.0.1-SNAPSHOT.jar
RUN echo ${JAR_FILE}
EXPOSE 9002
ENTRYPOINT ["java","-jar","/dan-ms-pedidos-0.0.1-SNAPSHOT.jar"]