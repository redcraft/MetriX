FROM maven

RUN apt-get update && apt-get install -y \
    npm \
&& rm -rf /var/lib/apt/lists/*

COPY ./ /opt/app/
WORKDIR /opt/app/

RUN ln -s /usr/bin/nodejs /usr/bin/node
RUN npm install grunt-cli -g
RUN npm install && grunt
RUN apt-get remove --purge npm -y && apt-get autoremove -y

EXPOSE 8080

CMD mvn jetty:run-war
