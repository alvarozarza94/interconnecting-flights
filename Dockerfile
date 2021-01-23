FROM openjdk:11 AS TEMP_BUILD_IMAGE

MAINTAINER alvarozarza94

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY . .
RUN ./gradlew build


FROM openjdk:11
ENV ARTIFACT_NAME=interconnecting-flights-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME interconnecting-flights-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/interconnecting-flights-0.0.1-SNAPSHOT.jar"]

