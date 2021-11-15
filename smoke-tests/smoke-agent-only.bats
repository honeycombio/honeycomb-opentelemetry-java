#!/usr/bin/env bats

load test_helpers/utilities

setup_file() {
	echo "# 🚧" >&3
	echo -n "# 🍿 Setting up smoke-agent-only ..." >&3
	docker-compose up --detach collector app-agent-only
	until [[ $(docker-compose logs app-agent-only | grep "OK I'm ready now") ]]; do sleep 1; done
	echo " ready. ✨"  >&3
}

setup() {
	curl --silent "http://localhost:5002"
	wait_for_data
}

teardown() {
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
