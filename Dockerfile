FROM agapito/centos7-scala:latest

WORKDIR /root

# install deps and create testdir
RUN yum -y install make && \
    mkdir -p /root/twitter-lm-extractor

WORKDIR /root/twitter-lm-extractor

COPY build.sbt /root/twitter-lm-extractor
COPY src /root/twitter-lm-extractor/src
COPY project /root/twitter-lm-extractor/project

# run tests
CMD ["sbt", "test"]
