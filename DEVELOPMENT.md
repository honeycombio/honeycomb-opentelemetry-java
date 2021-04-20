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
    implementation('io.honeycomb:sdk:1.0-SNAPSHOT')
}

```
