FROM ubuntu:18.04

ADD . /cth-core/apache-ofbiz-17.12.01
WORKDIR /cth-core/apache-ofbiz-17.12.01
# MAINTAINER

# Install OpenJDK-8
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant gradle && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
ENV JAVA_OPTS -Xmx3G
RUN export JAVA_HOME


# Setup backend connection
# RUN cat deploy/entity/entityengine.xml > framework/entity/config/entityengine.xml

##for passing in entity engine config - maybe replace with copy?
VOLUME /cth-core/apache-ofbiz-17.12.01/framework/entity/config/

##for Derby Database
VOLUME /cth-core/apache-ofbiz-17.12.01/runtime/data

EXPOSE 8443

# Run ofbiz
ENTRYPOINT ./gradlew build loadAll ofbiz