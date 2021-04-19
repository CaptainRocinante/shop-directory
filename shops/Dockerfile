FROM adoptopenjdk/openjdk11
ADD http://date.jsontest.com/ /tmp/bustcache
COPY target/shops-1.0-SNAPSHOT.jar shops-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/shops-1.0-SNAPSHOT.jar"]