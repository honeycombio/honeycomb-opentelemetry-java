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
./gradlew publishToMavenLocal -Pskip.signing
```

You can also specify a single build target:

```sh
./gradlew sdk:publishToMavenLocal -Pskip.signing
```

### Test signing

If you want to test signing the local artifacts, you can omit the `-Pskip-signing`, and export `GPG_BASE64` and `GPG_PASSPHRASE` to your shell env.
To get GPG_BASE64, you can generate a GPG key, and export the secret key:

```sh
gpg --armor --export-secret-keys KEY_ID | base64
```

Note: if you're not on OSX, you may need to use `base64 -w0` to disable line wrapping.

### Verify local artifacts

Your local Maven repository is located at `~/.m2/repository/`.
Go there to inspect your build artifacts:

```sh
$ cd ~/.m2/repository/io/honeycomb/honeycomb-opentelemetry-sdk/0.5.0
$ ls -1
honeycomb-opentelemetry-sdk-0.5.0.jar
honeycomb-opentelemetry-sdk-0.5.0.module
honeycomb-opentelemetry-sdk-0.5.0.pom
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
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.5.0')
}
```
