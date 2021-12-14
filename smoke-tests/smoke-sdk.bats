#!/usr/bin/env bats

load test_helpers/utilities

setup_file() {
	echo "# 🚧" >&3
	docker-compose up --detach collector app-sdk
	wait_for_ready_app 'app-sdk'
	curl --silent "http://localhost:5001"
	wait_for_traces
}

teardown_file() {
	docker-compose stop app-sdk
	docker-compose restart collector
	wait_for_flush
}

# TESTS

@test "Manual instrumentation produces span with name of span" {
	result=$(span_names_for 'examples')
	assert_equal "$result" '"greetings"'
}

@test "Manual instrumentation adds custom attribute" {
	result=$(span_attributes_for "examples" | jq "select(.key == \"custom_field\").value.stringValue")
	assert_equal "$result" '"important value"'
}


