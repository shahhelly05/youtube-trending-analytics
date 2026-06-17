#!/bin/bash
# Patch for Hive 4.0.0 Kryo Serialization Exception on Modern Java Runtimes
export HADOOP_CLIENT_OPTS="$HADOOP_CLIENT_OPTS --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED"

echo "Hadoop client options successfully patched for Hive 4.0.0!"