#!/usr/bin/env bats

setup_file() {
	echo "# setting up the tests ..." >&3
	poke
	wait_for_data
}

# TESTS

@test "Auto instrumentation produces a Spring controller span" {
	result=$(span_names_for "io.opentelemetry.spring-webmvc-3.1")
	[ "$result" = '"HelloController.index"' ]
}

@test "Auto instrumentation produces an incoming web request span" {
	result=$(span_names_for "io.opentelemetry.tomcat-7.0")
	[ "$result" = '"/"' ]
}

@test "Manual instrumentation produces span from @WithSpan annotation" {
	result=$(span_names_for "io.opentelemetry.opentelemetry-annotations-1.0")
	[ "$result" = '"importantSpan"' ]
}

@test "Manual instrumentation adds custom attribute" {
	result=$(span_attributes_for "io.opentelemetry.spring-webmvc-3.1" | jq "select(.key == \"custom_field\").value.stringValue")
	[ "$result" = '"important value"' ]
}

# UTILITY FUNCS

poke() {
	curl "http://app:5000"
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

# test span attributes
span_attributes_for() {
	# $1 - library name

	spans_from_library_named $1 | \
		jq ".attributes[]"
}

wait_for_data() {
	until [ "$(wc -l /var/lib/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "# Waiting for collector to receive data." >&3
		sleep 1
	done
}
