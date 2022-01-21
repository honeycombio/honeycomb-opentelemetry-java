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

print_project_version:=$(shell ./gradlew project_version -q)
#: display the project's version
project_version:
	@echo ${print_project_version}

build-artifacts:
	mkdir -p ./build-artifacts

smoke-tests/collector/data.json:
	@echo ""
	@echo "+++ Zhuzhing smoke test's Collector data.json"
	@touch $@ && chmod o+w $@

smoke-tests/apps/agent.jar: build-artifacts/honeycomb-opentelemetry-javaagent-${project_version}.jar
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

smoke-agent-only: smoke-tests/apps/spring-agent-only.jar smoke-tests/apps/agent.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-agent-only.bats --report-formatter junit --output ./

smoke-agent-manual: smoke-tests/apps/agent.jar smoke-tests/apps/spring-agent-manual.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-agent-manual.bats --report-formatter junit --output ./

smoke-sdk: smoke-tests/apps/agent.jar smoke-tests/apps/spring-sdk.jar smoke-tests/collector/data.json
	cd smoke-tests && bats ./smoke-sdk.bats --report-formatter junit --output ./

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

resmoke-sdk: unsmoke smoke-sdk

resmoke: unsmoke smoke

publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: build test publish_local smoke-agent-only unsmoke-agent-only resmoke-agent-only logs-agent-only smoke-agent-manual unsmoke-agent-manual resmoke-agent-manual logs-agent-manual smoke unsmoke resmoke
