SHELL=/bin/bash

build:
	./gradlew build

test:
	./gradlew test

clean:
	rm -rf ./build-artifacts/*
	rm -rf ./smoke-tests/apps/*.jar
	rm -rf ./smoke-tests/collector/data.json
	rm -rf ./smoke-tests/report.*
	./gradlew clean

project_version:=$(shell ./gradlew properties -q | grep "version:" | awk '{print $$2}')
#: display the project's version
project_version:
	@echo ${project_version}

build-artifacts:
	mkdir -p ./build-artifacts

smoke-tests/collector/data.json:
	@echo ""
	@echo "+++ Zhuzhing smoke test's Collector data.json"
	@touch $@ && chmod o+w $@

smoke-tests/apps/agent.jar: build-artifacts/honeycomb-opentelemetry-javaagent-$(project_version).jar
	@echo ""
	@echo "+++ Getting the agent in place for smoking."
	@echo ""
	cp $< $@

smoke-tests/apps/spring-agent-manual.jar: build-artifacts/spring-agent-manual-$(project_version).jar
	@echo ""
	@echo "+++ Getting the auto & manually instrumented app in place for smoking."
	@echo ""
	cp $< $@

smoke-tests/apps/spring-agent-only.jar: build-artifacts/spring-agent-only-$(project_version).jar
	@echo ""
	@echo "+++ Getting the auto-instrumented app in place for smoking."
	@echo ""
	cp $< $@

smoke-tests/apps/spring-sdk.jar: build-artifacts/spring-sdk-$(project_version).jar
	@echo ""
	@echo "+++ Getting the SDK manually-instrumented app in place for smoking."
	@echo ""
	cp $< $@

.PHONY: smoke-agent-only
smoke-agent-only: smoke-agent-grpc smoke-agent-http

.PHONY: smoke-agent-grpc
smoke-agent-grpc: smoke-tests/apps/spring-agent-only.jar smoke-tests/apps/agent.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-agent-grpc.bats --report-formatter junit --output ./

.PHONY: smoke-agent-http
smoke-agent-http: smoke-tests/apps/spring-agent-only.jar smoke-tests/apps/agent.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-agent-http.bats --report-formatter junit --output ./

smoke-agent-manual: smoke-tests/apps/agent.jar smoke-tests/apps/spring-agent-manual.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-agent-manual.bats --report-formatter junit --output ./

.PHONY: smoke-sdk
smoke-sdk: smoke-sdk-grpc smoke-sdk-http

.PHONY: smoke-sdk-grpc
smoke-sdk-grpc: smoke-tests/apps/agent.jar smoke-tests/apps/spring-sdk.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-sdk-grpc.bats --report-formatter junit --output ./

.PHONY: smoke-sdk-http
smoke-sdk-http: smoke-tests/apps/agent.jar smoke-tests/apps/spring-sdk.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-sdk-http.bats --report-formatter junit --output ./

smoke: smoke-tests/apps/spring-sdk.jar smoke-tests/apps/spring-agent-manual.jar smoke-tests/apps/spring-agent-only.jar smoke-tests/apps/agent.jar smoke-tests/collector/data.json
	@echo ""
	@echo "+++ Smoking all the tests."
	@echo ""
	cd smoke-tests && bats . --report-formatter junit --output ./

unsmoke:
	@echo ""
	@echo "+++ Spinning down the smokers."
	@echo ""
	cd smoke-tests && docker-compose down --volumes

resmoke-agent-only: unsmoke smoke-agent-only

resmoke-agent-manual: unsmoke smoke-agent-manual

.PHONY: resmoke-sdk
resmoke-sdk: unsmoke smoke-sdk

.PHONY: resmoke-sdk-grpc
resmoke-sdk-grpc: unsmoke smoke-sdk-grpc

.PHONY: resmoke-sdk-http
resmoke-sdk-http: unsmoke smoke-sdk-http

resmoke: unsmoke smoke

publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: build test publish_local smoke-agent-only unsmoke-agent-only resmoke-agent-only logs-agent-only smoke-agent-manual unsmoke-agent-manual resmoke-agent-manual logs-agent-manual smoke unsmoke resmoke
