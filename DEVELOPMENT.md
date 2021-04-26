# Local Development

## Publish jar to local Maven repository

Configure `build.gradle` to publish to Maven.
(The "local" part is specified in build command.)
This must be configured separately for each module (agent, sdk, and common).

```groovy
// ./myModule/build.gradle
plugins {
    id "maven-publish" 
}

def artifactName = "honeycomb-opentelemetry-${yourModule}"

jar {
    archivesBaseName = "${artifactName}"
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            artifactId = "${artifactName}"
            from components.java
        }
    }
}
```

From project root, build all jars and publish to local maven repository:

```sh
./gradlew publishToMavenLocal
```

You can also specify a single build target:

```sh
./gradlew sdk:publishToMavenLocal
```

Your local Maven repository is located at `~/.m2/repository/`.
Go there to inspect your build artifacts:

```sh
$ cd ~/.m2/repository/io/honeycomb/honeycomb-opentelemetry-sdk/1.0-SNAPSHOT
$ ls -1
honeycomb-opentelemetry-sdk-1.0-SNAPSHOT.jar
honeycomb-opentelemetry-sdk-1.0-SNAPSHOT.module
honeycomb-opentelemetry-sdk-1.0-SNAPSHOT.pom
maven-metadata-local.xml
```

## Use packages published to Maven local

Configure your test application's Gradle file
to point to your local Maven repository:

```groovy
// ~/exampleApp/build.gradle
repositories {
    mavenCentral()
    // etc.
    mavenLocal()
}

dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0')
}
```
