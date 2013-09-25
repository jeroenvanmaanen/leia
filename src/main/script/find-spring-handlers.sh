#!/bin/bash

SPRING_HANDLERS='src/main/resources/META-INF/spring.handlers'
rm -f "${SPRING_HANDLERS}"
touch "${SPRING_HANDLERS}"

mvn dependency:build-classpath \
	| grep -v '^[[]' \
	| tr ':' '\012' \
	| while read JAR
		do
			echo ">>> JAR=[${JAR}]" >&2
			if [ -n "$(unzip -l "${JAR}" | grep 'spring.handlers')" ]
			then
				echo '... Match!' >&2
				unzip -p "${JAR}" META-INF/spring.handlers >> "${SPRING_HANDLERS}"
			fi
		done

cat "${SPRING_HANDLERS}"
