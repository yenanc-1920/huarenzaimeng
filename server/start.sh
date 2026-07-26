#!/bin/sh
exec java -jar -Xms256m -Xmx512m \
  -Duser.timezone=UTC \
  /app/app.jar
