.PHONY: build
build:
	./gradlew build

.PHONY: test
test:
	./gradlew test

.PHONY: publish_local
publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: smoke_test
smoke_test:
	./gradlew -p examples/distro/smoke-tests test
