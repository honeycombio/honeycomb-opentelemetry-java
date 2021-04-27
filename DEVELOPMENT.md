# Local Development

Publishing the SDK to local maven repository:

```sh
./gradlew sdk:publishToMavenLocal
```

Test application Gradle file:

```groovy
repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation('io.honeycomb:honeycomb-opentelemetry-sdk:0.1.0')
}

```
