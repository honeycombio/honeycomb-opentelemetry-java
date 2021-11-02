SHELL=/bin/bash

build:
	./gradlew build

test:
	./gradlew test

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

.PHONY: build test publish_local

