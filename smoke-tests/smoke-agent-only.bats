#!/usr/bin/env bats

load test_helpers/utilities

setup_file() {
	echo "# 🚧" >&3
	docker-compose up --detach collector app-agent-only
	wait_for_ready_app 'app-agent-only'
	curl --silent "http://localhost:5002"
	wait_for_data
}

teardown_file() {
	docker-compose restart collector
	wait_for_flush
}

# TESTS

@test "Auto instrumentation produces a Spring controller span" {
	result=$(span_names_for "io.opentelemetry.spring-webmvc-3.1")
	# assert_equal "$result" '"HelloController.index"'
	assert_equal "$result" '"test fail smoke-agent-only"'
}

@test "Auto instrumentation produces an incoming web request span" {
	result=$(span_names_for "io.opentelemetry.tomcat-7.0")
	assert_equal "$result" '"/"'
}

@test "Auto instrumentation emits metrics" {
	wait_for_metrics 12
	metric_names=$( metrics_received | jq ".instrumentationLibraryMetrics[].metrics[].name" | wc -l | awk '{ print $1}' )
	[ "$metric_names" -ne 0 ]
}
