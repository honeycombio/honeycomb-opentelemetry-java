build:
	./gradlew build

test:
	./gradlew test

publish_local:
	./gradlew publishToMavenLocal -Pskip.signing

.PHONY: build test publish_local
