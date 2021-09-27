# Creating a new release

1. First, update the `project.version` in the root build.gradle file. Update the version in `DistroMetadata.java`. Also update the Changelog, as well as any references to the previous version in the docs.

2. If the new release updates the OpenTelemetry SDK and/or agent versions, update the `Latest release built with` section in the [README](./README.md).

3. Once the above changes are merged into `main`, tag `main` with the new version, e.g. `v0.1.1`. Push the tags. This will kick off CI, which will publish a draft GitHub release, and stage the new release in [Sonatype](https://oss.sonatype.org).

4. You should now see the new release in Sonatype UI under `Staging Repositories`. Select the release, and "Close" it. That will do some validation.

5. Once the repo is closed, you can test the staged artifacts by pointing a test app at the staging group:

    ```groovy
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/groups/staging/")
        }
    }
    ```

6. Once the staged repo is closed, you should be able to "Release" it to make it available in Maven. See more details in [Sonatype docs](https://help.sonatype.com/repomanager2/staging-releases/managing-staging-repositories).

7. Update Release Notes on the new draft GitHub release, and publish that.

8. Update public docs with the new version.

Voila!
