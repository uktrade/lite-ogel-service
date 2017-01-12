FROM java:8

ENV JAR_FILE lite-ogel-service-1.0.jar
ENV CONFIG_FILE /conf/ogel-service-config.yaml

WORKDIR /opt/lite-ogel-service

COPY build/libs/$JAR_FILE /opt/lite-ogel-service

CMD java "-jar" $JAR_FILE "server" $CONFIG_FILE