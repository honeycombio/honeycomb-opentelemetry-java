#!/usr/bin/env bats

load test_helpers/utilities

setup_file() {
	echo "# setting up the tests ..." >&3
	docker-compose up --detach collector app-agent-manual
	until [[ $(docker-compose logs app-agent-manual | grep "OK I'm ready now") ]]; do sleep 1; done
}

setup() {
	curl --silent "http://localhost:5000"
	wait_for_data
}

teardown() {
	docker-compose restart collector
	wait_for_flush
}

# TESTS

@test "Auto instrumentation produces a Spring controller span" {
	result=$(span_names_for "io.opentelemetry.spring-webmvc-3.1")
	# assert_equal "$result" '"HelloController.index"'
	assert_equal "$result" '"NopeController.index"' # intentional failure to see what it looks like in CI
}

@test "Auto instrumentation produces an incoming web request span" {
	result=$(span_names_for "io.opentelemetry.tomcat-7.0")
	assert_equal "$result" '"/"'
}

@test "Manual instrumentation produces span from @WithSpan annotation" {
	result=$(span_names_for "io.opentelemetry.opentelemetry-annotations-1.0")
	assert_equal "$result" '"importantSpan"'
}

@test "Manual instrumentation adds custom attribute" {
	result=$(span_attributes_for "io.opentelemetry.spring-webmvc-3.1" | jq "select(.key == \"custom_field\").value.stringValue")
	assert_equal "$result" '"important value"'
}
