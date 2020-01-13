FROM openjdk:8-jre-alpine

ENV VERTICLE_FILE fhir-1.0.0-SNAPSHOT-fat.jar

# Set the location of the verticles inside the container
ENV VERTICLE_HOME /usr/verticles


EXPOSE 8000

# Copy your fat jar to the container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]


