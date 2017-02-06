#!/bin/bash
set -u
set -e

path=$PWD/"src/main/resources"

touch "$path/twitter.conf"

cat > "$path"/twitter.conf <<-EOF
twitter {
  consumer {
    key = "$TWITTER_CONSUMER_KEY"
    secret = "$TWITTER_CONSUMER_SECRET"
  }
  access {
    key = "$TWITTER_ACCESS_KEY"
    secret = "$TWITTER_ACCESS_SECRET"
  }
}
EOF
