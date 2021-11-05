#!/usr/bin/env bats

setup_file() {
	echo "# setting up the tests ..." >&3
	poke
	wait_for_data
}

# TESTS

@test "Auto instrumentation produces a Spring controller span" {
	span_names_for "io.opentelemetry.spring-webmvc-3.1" | grep "HelloController.index"
}

@test "Auto instrumentation produces an incoming web request span" {
	span_names_for "io.opentelemetry.tomcat-7.0" | grep "/"
}

# UTILITY FUNCS

poke() {
	curl "http://app:5002"
}

span_names_for() {
	jq ".resourceSpans[] |
			.instrumentationLibrarySpans[] |
			select(.instrumentationLibrary.name == \"$1\").spans[].name" \
		/var/lib/data.json
}

wait_for_data() {
	until [ "$(wc -l /var/lib/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "# Waiting for collector to receive data." >&3
		sleep 1
	done
}
