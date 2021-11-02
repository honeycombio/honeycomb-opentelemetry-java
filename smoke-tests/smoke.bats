#!/usr/bin/env bats

# TEST SETUP

# before_all
# setup_file() {
#     echo "# APP_ENDPOINT: $APP_ENDPOINT" >&3
#     echo "# AGENT_JAR: $AGENT_JAR" >&3
#     echo "# APP_JAR: $APP_JAR" >&3

# 	if [[ -z "${APP_ENDPOINT}" ]]; then
# 		echo "# APP_ENDPOINT is not defined, bailing." >&3
# 		exit 1
# 	fi

# 	if [[ -z "${AGENT_JAR}" ]]; then
# 		echo "# AGENT_JAR is not defined, bailing." >&3
# 		exit 1
# 	fi

# 	if [[ -z "${APP_JAR}" ]]; then
# 		echo "# APP_JAR is not defined, bailing." >&3
# 		exit 1
# 	fi

# 	echo "# Starting up containers for test ..." >&3

# 	docker-compose up --detach
# 	until [[ $(docker-compose logs app | grep "OK I'm ready now") ]]
# 	do
# 		echo "# Waiting for instrumented app to become ready." >&3
# 		sleep 0.1
# 	done
# }

# before_each
# setup() {
# 	docker-compose up --detach
# }

# after_each
teardown() {
	docker-compose restart collector
	until [ "$(wc -l output/data.json | awk '{ print $1 }')" -eq 0 ]
	do
		echo "# Waiting for collector data flush." >&3
		sleep 0.1
	done
}

# after_all
# teardown_file() {
# 	echo "# Shutting down test containers ..." >&3
# 	docker-compose down
# }


# TESTS

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

# UTILITY FUNCS

poke() {
	curl $APP_ENDPOINT
}

span_names_for() {
	jq ".resourceSpans[] |
			.instrumentationLibrarySpans[] |
			select(.instrumentationLibrary.name == \"$1\").spans[].name" \
		data-volume:/output/data.json
}

wait_for_data() {
	until [ "$(wc -l output/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "# Waiting for collector to receive data." >&3
		sleep 0.1
	done
}
