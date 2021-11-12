#!/usr/bin/env bats

load test_helpers/utilities.bash

setup_file() {
	echo "# setting up the tests ..." >&3
	curl "http://app-agent-only:5002"
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
