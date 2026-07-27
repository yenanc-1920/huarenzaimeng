#!/bin/sh
shutdown() {
    echo "[start.sh] SIGTERM received, shutting down gracefully..."
    kill -TERM "$JAVA_PID" 2>/dev/null
    wait "$JAVA_PID"
    echo "[start.sh] Application stopped."
    exit 0
}
trap shutdown TERM INT

java -jar -Xms256m -Xmx512m \
  -Duser.timezone=UTC \
  /app/app.jar &
JAVA_PID=$!

wait "$JAVA_PID"
