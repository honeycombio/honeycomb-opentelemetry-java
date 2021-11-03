SHELL=/bin/bash

build:
	./gradlew build

test:
	./gradlew test

project_version:=$(shell ./gradlew --quiet --console=plain project_version)
#: display the project's version
project_version:
	@echo ${project_version}

dc=docker-compose --file ./smoke-tests/docker-compose.yml
smoke:
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
