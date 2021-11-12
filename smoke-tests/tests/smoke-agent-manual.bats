#!/usr/bin/env bats

load test_helpers/utilities.bash

setup_file() {
	echo "# setting up the tests ..." >&3
	curl "http://app-agent-manual:5000"
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
