SHELL=/bin/bash

build:
	./gradlew build

test:
	./gradlew test

project_version:=$(shell ./gradlew --quiet --console=plain project_version)
#: display the project's version
project_version:
	@echo ${project_version}

agent/build/libs/honeycomb-opentelemetry-javaagent-${project_version}-all.jar:
	./gradlew --console=plain :agent:shadowJar

build-artifacts/honeycomb-opentelemetry-javaagent-${project_version}-all.jar: agent/build/libs/honeycomb-opentelemetry-javaagent-${project_version}-all.jar
	cp ./agent/build/libs/honeycomb-opentelemetry-javaagent-${project_version}-all.jar ./build-artifacts

smoke-tests/agent.jar: build-artifacts/honeycomb-opentelemetry-javaagent-${project_version}-all.jar
	cp ./build-artifacts/honeycomb-opentelemetry-javaagent-${project_version}-all.jar ./smoke-tests/agent.jar

examples/spring-agent-only/build/libs/spring-agent-only-${project_version}.jar:
	./gradlew --console=plain :examples:spring-agent-only:build

build-artifacts/spring-agent-only-${project_version}.jar: examples/spring-agent-only/build/libs/spring-agent-only-${project_version}.jar
	cp ./examples/spring-agent-only/build/libs/spring-agent-only-${project_version}.jar ./build-artifacts

smoke-tests/app.jar: build-artifacts/spring-agent-only-${project_version}.jar
	cp ./build-artifacts/spring-agent-only-${project_version}.jar smoke-tests/app.jar

dc=docker-compose --file ./smoke-tests/docker-compose.yml
#: run the smoke tests
smoke: smoke-tests/agent.jar smoke-tests/app.jar
	${dc} up --detach --build collector app
	until [[ $$(${dc} logs app | grep "OK I'm ready now") ]]; do sleep 1; done
	${dc} up --build --exit-code-from bats bats

unsmoke:
	${dc} down --volumes

resmoke: unsmoke smoke

logs:
	${dc} logs

publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: build test publish_local project_version smoke unsmoke resmoke logs
