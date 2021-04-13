FROM adoptopenjdk/openjdk11
RUN apt-get -y update
RUN apt-get install -yqq unzip curl wget
RUN wget -O /tmp/chromedriver.zip http://chromedriver.storage.googleapis.com/`curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE`/chromedriver_linux64.zip
RUN unzip /tmp/chromedriver.zip chromedriver -d /usr/local/bin/
COPY target/shops-1.0-SNAPSHOT.jar shops-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/shops-1.0-SNAPSHOT.jar"]