# Creating a new release

1. First, update the `project.version` in the root build.gradle file. Also update the Changelog, as well as any references to the previous version in the docs.

1. Once the above changes are merged into `main`, tag `main` with the new version, e.g. `v0.1.1`. Push the tags. This will kick off CI, which will publish a draft GitHub release, and stage the new release in [Sonatype](https://oss.sonatype.org).

1. You should now see the new release in Sonatype UI under `Staging Repositories`. Select the release, and "Close" it. That will do some validation. Once that's done, you should be able to "Release" it to make it available in Maven. See more details in [Sonatype docs](https://help.sonatype.com/repomanager2/staging-releases/managing-staging-repositories).

1. Update Release Notes on the new draft GitHub release, and publish that.

Voila!
