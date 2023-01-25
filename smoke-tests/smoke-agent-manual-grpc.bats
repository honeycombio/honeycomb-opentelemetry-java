#!/usr/bin/env bats

load test_helpers/utilities

CONTAINER_NAME="app-agent-manual-grpc"

setup_file() {
	echo "# 🚧" >&3
	docker-compose up --detach collector ${CONTAINER_NAME}
	wait_for_ready_app ${CONTAINER_NAME}
	curl --silent "http://localhost:5000"
	wait_for_traces
}

teardown_file() {
  cp collector/data.json collector/data-results/data-${CONTAINER_NAME}.json
	docker-compose stop ${CONTAINER_NAME}
	docker-compose restart collector
	wait_for_flush
}

# TESTS

@test "Auto instrumentation produces a Spring controller span" {
	result=$(span_names_for "io.opentelemetry.spring-webmvc-3.1")
	assert_equal "$result" '"HelloController.index"'
}

@test "Auto instrumentation produces an incoming web request span" {
	result=$(span_names_for "io.opentelemetry.tomcat-7.0")
	assert_equal "$result" '"/"'
}

@test "Manual instrumentation produces span from @WithSpan annotation" {
	result=$(span_names_for "io.opentelemetry.opentelemetry-instrumentation-annotations-1.16")
	assert_equal "$result" '"importantSpan"'
}

@test "Manual instrumentation adds custom attribute" {
	result=$(span_attributes_for "io.opentelemetry.spring-webmvc-3.1" | jq "select(.key == \"custom_field\").value.stringValue")
	assert_equal "$result" '"important value"'
}

@test "BaggageSpanProcessor: key-values added to baggage appear on child spans" {
	result=$(span_attributes_for "io.opentelemetry.opentelemetry-instrumentation-annotations-1.16" | jq "select(.key == \"for_the_children\").value.stringValue")
	assert_equal "$result" '"another important value"'
}
