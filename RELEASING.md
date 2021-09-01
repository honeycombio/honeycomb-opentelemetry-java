# Creating a new release

1. First, update the `project.version` in the root build.gradle file. Also update the Changelog, as well as any references to the previous version in the docs.

2. Once the above changes are merged into `main`, tag `main` with the new version, e.g. `v0.1.1`. Push the tags. This will kick off CI, which will publish a draft GitHub release, and stage the new release in [Sonatype](https://oss.sonatype.org).

3. You should now see the new release in Sonatype UI under `Staging Repositories`. Select the release, and "Close" it. That will do some validation.

4. Once the repo is closed, you can test the staged artifacts by pointing a test app at the staging group:

    ```groovy
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/groups/staging/")
        }
    }
    ```

5. Once the staged repo is closed, you should be able to "Release" it to make it available in Maven. See more details in [Sonatype docs](https://help.sonatype.com/repomanager2/staging-releases/managing-staging-repositories).

6. Update Release Notes on the new draft GitHub release, and publish that.

7. Update public docs with the new version.

Voila!
