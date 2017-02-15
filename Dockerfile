FROM agapito/centos7-scala:latest

COPY . /twitter-lm-extractor/

WORKDIR /twitter-lm-extractor

ENTRYPOINT ["sbt", "-Djava.util.logging.config.file=./src/main/resources/logging.properties", "-Dsbt.log.noformat=true", "clean", "scalastyle", "test:scalastyle", "test"]
