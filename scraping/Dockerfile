FROM adoptopenjdk/openjdk11
RUN apt-get -y update
RUN apt-get install -yqq unzip curl wget
RUN apt-get install -y gconf-service gnupg libasound2 libatk1.0-0 libcairo2 libcups2 \
libfontconfig1 libgdk-pixbuf2.0-0 libgtk-3-0 libnspr4 libpango-1.0-0 libxss1 fonts-liberation libappindicator1 libnss3 lsb-release xdg-utils

# install google chrome
RUN wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_89.0.4389.128-1_amd64.deb
RUN dpkg -i google-chrome-stable_89.0.4389.128-1_amd64.deb; apt-get -fy install

# install chromedriver
RUN apt-get install -yqq unzip
RUN wget -O /tmp/chromedriver.zip http://chromedriver.storage.googleapis.com/89.0.4389.23/chromedriver_linux64.zip
RUN unzip /tmp/chromedriver.zip chromedriver -d /usr/local/bin/

# set display port to avoid crash
ENV DISPLAY=:99

ADD http://date.jsontest.com/ /tmp/bustcache
COPY target/scraping-1.0-SNAPSHOT.jar scraping-1.0-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/scraping-1.0-SNAPSHOT.jar"]