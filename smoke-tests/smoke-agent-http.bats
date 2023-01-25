#!/usr/bin/env bats

load test_helpers/utilities

CONTAINER_NAME="smoke-agent-http"

setup_file() {
	echo "# 🚧" >&3
	docker-compose up --detach collector ${CONTAINER_NAME}
	wait_for_ready_app ${CONTAINER_NAME}
	curl --silent "http://localhost:5002"
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

@test "Auto instrumentation emits metrics" {
	wait_for_metrics 12
	metric_names=$( metrics_received | jq ".scopeMetrics[].metrics[].name" | wc -l | awk '{ print $1}' )
	[ "$metric_names" -ne 0 ]
}
