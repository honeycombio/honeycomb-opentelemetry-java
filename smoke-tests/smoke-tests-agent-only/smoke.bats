#!/usr/bin/env bats

setup_file() {
	echo "# setting up the tests ..." >&3
	poke
	wait_for_data
}

# TESTS

@test "Auto instrumentation produces a Spring controller span" {
	result=$(span_names_for "io.opentelemetry.spring-webmvc-3.1")
	echo "# result: $result" >&3
	[ "$result" = '"HelloController.index"' ]
}

@test "Auto instrumentation produces an incoming web request span" {
	result=$(span_names_for "io.opentelemetry.tomcat-7.0")
	echo "# result: $result" >&3
	[ "$result" = '"/"' ]
}

# UTILITY FUNCS

poke() {
	curl "http://app:5002"
}

spans_from_library_named() {
	jq ".resourceSpans[] |
			.instrumentationLibrarySpans[] |
			select(.instrumentationLibrary.name == \"$1\").spans[]" \
		/var/lib/data.json
}

# test span name
span_names_for() {
	spans_from_library_named $1 | jq '.name'
}

wait_for_data() {
	until [ "$(wc -l /var/lib/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "# Waiting for collector to receive data." >&3
		sleep 1
	done
}
