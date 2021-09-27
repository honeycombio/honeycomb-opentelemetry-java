# Creating a new release

1. First, update the `project.version` in the root build.gradle file. Update the version in `DistroMetadata.java`. Also update the Changelog, as well as any references to the previous version in the docs.

2. If the new release updates the OpenTelemetry SDK and/or agent versions, update the `Latest release built with` section in the [README](./README.md).

3. Once the above changes are merged into `main`, tag `main` with the new version, e.g. `v0.1.1`. Push the tags. This will kick off CI, which will publish a draft GitHub release, and publish to Maven.

4. Update Release Notes on the new draft GitHub release, and publish that.

5. Update public docs with the new version.

Voila!
