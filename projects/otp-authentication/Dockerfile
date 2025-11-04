# This layer sets up to create a prod ready container
# A default build of this Dockerfile will result in this layer being included.
# First stage, build the custom JRE
FROM eclipse-temurin:21-jdk-jammy AS builder

# Install binutils, required by jlink
RUN apt-get update -y &&  \
    apt-get install -y binutils

RUN mkdir /home/app

WORKDIR /home/app

COPY ./target/otp-authentication.jar ./app.jar

RUN jar xvf app.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 17  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    app.jar > modules.txt

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules $(cat modules.txt) \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /optimized-jdk-21

# Second stage, Use the custom JRE and build the app image
FROM alpine:latest AS runner
# FROM ubuntu:22.04 AS runner

LABEL authors="ShortThirdMan"

ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy JRE from the base image
COPY --from=builder /optimized-jdk-21 $JAVA_HOME

# Add app user
ARG APP_USER=javauser

# Create a user to run the application, don't run as root
RUN addgroup --system $APP_USER &&  adduser --system $APP_USER --ingroup $APP_USER --shell /bin/false

# Create the application directory
RUN mkdir /home/app && chown -R $APP_USER /home/app

WORKDIR /home/app

# RUN apt-get update && apt-get install -y dumb-init curl
RUN apk --no-cache add curl dumb-init && \
	rm -rf /var/cache/apk/*

USER $APP_USER

ENV JAVA_OPTS="$JAVA_OPTS -XX:+UseContainerSupport -XX:+UseG1GC"

EXPOSE 8080
CMD ["dumb-init", "java", "$JAVA_OPTS", "-jar", "/home/app/app.jar"]