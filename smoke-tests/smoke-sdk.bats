#!/usr/bin/env bats

load test_helpers/utilities.bash

setup_file() {
	echo "# setting up the tests ..." >&3
	docker-compose up --detach collector app-sdk
	until [[ $(docker-compose logs app-sdk | grep "OK I'm ready now") ]]; do sleep 1; done
}

setup() {
	curl --silent "http://localhost:5001"
	wait_for_data
}

teardown() {
	docker compose restart collector
	wait_for_flush
}

# TESTS

@test "Manual instrumentation produces span with name of span" {
	result=$(span_names_for 'examples')
	[ "$result" = '"greetings"' ]
}

@test "Manual instrumentation adds custom attribute" {
	result=$(span_attributes_for "examples" | jq "select(.key == \"custom_field\").value.stringValue")
	[ "$result" = '"important value"' ]
}


