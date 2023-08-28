#!/bin/sh

exec java $JAVA_OPTS $JAVA_TOOL_OPTS -cp @/app/jib-classpath-file @/app/jib-main-class-file