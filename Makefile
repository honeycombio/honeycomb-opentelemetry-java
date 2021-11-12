SHELL=/bin/bash

build:
	./gradlew build

test:
	./gradlew test

clean:
	rm -rf ./build-artifacts/*
	rm -rf ./smoke-tests/jars/*
	./gradlew clean

project_version:=$(shell grep 'project.version =' build.gradle | awk -F\" '{ print $$2 }')
#: display the project's version
project_version:
	@echo ${project_version}

dc-smoke-tests=docker-compose --file ./smoke-tests/docker-compose.yml
dc-agent-only=${dc-smoke-tests} --project-name smoke-tests-agent-only
dc-agent-manual=${dc-smoke-tests} --project-name smoke-tests-agent-manual
dc-sdk=${dc-smoke-tests} --project-name smoke-tests-sdk

build-artifacts:
	mkdir -p ./build-artifacts

smoke-tests/jars:
	mkdir -p ./smoke-tests/jars

smoke-tests/jars/agent.jar: build-artifacts/honeycomb-opentelemetry-javaagent-${project_version}-all.jar smoke-tests/jars
	cp $< $@

smoke-tests/jars/spring-agent-manual.jar: build-artifacts/spring-agent-manual-$(project_version).jar smoke-tests/jars
	cp $< $@

smoke-tests/jars/spring-agent-only.jar: build-artifacts/spring-agent-only-$(project_version).jar smoke-tests/jars
	cp $< $@

smoke-tests/jars/spring-sdk.jar: build-artifacts/spring-sdk-$(project_version).jar smoke-tests/jars
	cp $< $@

smoke-agent-only: smoke-tests/jars/spring-agent-only.jar smoke-tests/jars/agent.jar
	${dc-agent-only} up --detach --build collector app-agent-only
	until [[ $$(${dc-agent-only} logs app-agent-only | grep "OK I'm ready now") ]]; do sleep 1; done
	${dc-agent-only} up --build --exit-code-from bats-agent-only bats-agent-only

smoke-agent-manual: smoke-tests/jars/agent.jar smoke-tests/jars/spring-agent-manual.jar
	${dc-agent-manual} up --detach --build collector app-agent-manual
	until [[ $$(${dc-agent-manual} logs app-agent-manual | grep "OK I'm ready now") ]]; do sleep 1; done
	${dc-agent-manual} up --build --exit-code-from bats-agent-manual bats-agent-manual

smoke-sdk: smoke-tests/jars/agent.jar smoke-tests/jars/spring-sdk.jar
	${dc-sdk} up --detach --build collector app-sdk
	until [[ $$(${dc-sdk} logs app-sdk | grep "OK I'm ready now") ]]; do sleep 1; done
	${dc-sdk} up --build --exit-code-from bats-sdk bats-sdk

smoke: smoke-agent-only smoke-agent-manual smoke-sdk

unsmoke-agent-only:
	${dc-agent-only} down --volumes

unsmoke-agent-manual:
	${dc-agent-manual} down --volumes

unsmoke-sdk:
	${dc-sdk} down --volumes

unsmoke: unsmoke-agent-only unsmoke-agent-manual unsmoke-sdk

resmoke-agent-only: unsmoke-agent-only smoke-agent-only

resmoke-agent-manual: unsmoke-agent-manual smoke-agent-manual

resmoke-sdk: unsmoke-sdk smoke-sdk

resmoke: resmoke-agent-only resmoke-agent-manual resmoke-sdk

logs-agent-only:
	${dc-agent-only} logs

logs-agent-manual:
	${dc-agent-manual} logs

logs-sdk:
	${dc-sdk} logs

publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: build test publish_local smoke-agent-only unsmoke-agent-only resmoke-agent-only logs-agent-only smoke-agent-manual unsmoke-agent-manual resmoke-agent-manual logs-agent-manual smoke unsmoke resmoke
