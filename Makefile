SHELL=/bin/bash

build:
	./gradlew build

test:
	./gradlew test

project_version:=$(shell grep 'project.version =' build.gradle | awk -F\" '{ print $$2 }')
#: display the project's version
project_version:
	@echo ${project_version}

dc-agent-only=docker-compose --file ./smoke-tests/smoke-tests-agent-only/docker-compose.yml
dc-agent-manual=docker-compose --file ./smoke-tests/smoke-tests-agent-manual/docker-compose.yml

smoke-agent-only:
	${dc-agent-only} up --detach --build collector app
	until [[ $$(${dc-agent-only} logs app | grep "OK I'm ready now") ]]; do sleep 1; done
	${dc-agent-only} up --build --exit-code-from bats bats

smoke-agent-manual:
	${dc-agent-manual} up --detach --build collector app
	until [[ $$(${dc-agent-manual} logs app | grep "OK I'm ready now") ]]; do sleep 1; done
	${dc-agent-manual} up --build --exit-code-from bats bats

smoke: smoke-agent-only smoke-agent-manual

unsmoke-agent-only:
	${dc-agent-only} down --volumes

unsmoke-agent-manual:
	${dc-agent-manual} down --volumes

unsmoke: unsmoke-agent-only unsmoke-agent-manual

resmoke-agent-only: unsmoke-agent-only smoke-agent-only

resmoke-agent-manual: unsmoke-agent-manual smoke-agent-manual

resmoke: resmoke-agent-only resmoke-agent-manual

logs-agent-only:
	${dc-agent-only} logs

logs-agent-manual:
	${dc-agent-manual} logs

publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: build test publish_local smoke-agent-only unsmoke-agent-only resmoke-agent-only logs-agent-only smoke-agent-manual unsmoke-agent-manual resmoke-agent-manual logs-agent-manual smoke unsmoke resmoke
