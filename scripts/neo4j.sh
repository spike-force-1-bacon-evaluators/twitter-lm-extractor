#!/bin/bash
set -u
set -e

path=$PWD/"src/main/resources"

touch "$path/neo4j.conf"

cat > "$path"/neo4j.conf <<-EOF
neo4j {
   host = "localhost"
   boltport = "7687"
   username = "$NEO4J_USERNAME"
   password = "$NEO4J_PASSWORD"
}
EOF
