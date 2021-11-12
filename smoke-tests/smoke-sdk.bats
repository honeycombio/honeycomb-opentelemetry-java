#!/usr/bin/env bats

load test_helpers/utilities.bash

setup_file() {
	echo "# setting up the tests ..." >&3
	curl "http://app-sdk:5001"
	wait_for_data
}

# TESTS

@test "Manual instrumentation produces span with name of span" {
	result=$(span_names_for 'examples')
	echo "# result: $result" >&3
	[ "$result" = '"greetings"' ]
}

@test "Manual instrumentation adds custom attribute" {
	result=$(span_attributes_for "examples" | jq "select(.key == \"custom_field\").value.stringValue")
	[ "$result" = '"important value"' ]
}


