#!/bin/sh
JAR=$(find /home/ec2-user/ -iname '*.jar' -type -f)
if [ $JAR ]; then
  rm /home/ec2-user/*.jar
fi
