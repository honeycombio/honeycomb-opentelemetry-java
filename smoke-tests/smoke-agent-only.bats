#!/usr/bin/env bats

load test_helpers/utilities

setup_file() {
	echo "# setting up the tests ..." >&3
	docker-compose up --detach collector app-agent-only
	until [[ $(docker-compose logs app-agent-only | grep "OK I'm ready now") ]]; do sleep 1; done
}

setup() {
	curl "http://localhost:5002"
	wait_for_data
}

teardown() {
	docker-compose restart collector
	wait_for_flush
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
