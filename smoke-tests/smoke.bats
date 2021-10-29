#!/usr/bin/env bats

setup_file() {
	echo "# Starting up containers for test ..." >&3

	docker compose up --detach
	until [[ $(docker compose logs app | grep "OK I'm ready now") ]]
	do
		echo "Waiting for instrumented app to become ready."
		sleep 0.1
	done
}

setup_file() {
    if [[ -z "${APP_ENDPOINT}" ]]; then
      echo "APP_ENDPOINT is not defined, bailing."
      exit 1
    fi
}

setup() {
	docker compose up --detach
}

teardown() {
	docker compose restart collector
	until [ "$(wc -l output/data.json | awk '{ print $1 }')" -eq 0 ]
	do
		echo "Waiting for collector data flush."
		sleep 0.1
	done
}

wait_for_data() {
	until [ "$(wc -l output/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "Waiting for collector to receive data."
		sleep 0.1
	done
}

poke() {
	curl $APP_ENDPOINT
}

span_names_for() {
	jq ".resourceSpans[] |
			.instrumentationLibrarySpans[] |
			select(.instrumentationLibrary.name == \"$1\").spans[].name" \
		output/data.json
}

@test "Auto instrumentation produces a Spring controller span" {
	poke
	wait_for_data
	span_names_for "io.opentelemetry.spring-webmvc-3.1" | grep "HelloController.index"
}

@test "Auto instrumentation produces an incoming web request span" {
	poke
	wait_for_data
	span_names_for "io.opentelemetry.tomcat-7.0" | grep "/"
}
