# Creating a new release

1. Prep public docs PR with the new version (java-distro, otel-java-configuration, otel-java-install, otel-java-run)

1. Prep Onboarding docs PR with the new version (hound/DocsJava)

1. Update the `project.version` in the root build.gradle file with the new release version. Snapshot version is one patch bump ahead of the new release (e.g. if we're releasing `1.0.0` then the corresponding snapshot would be `1.0.1`)

1. Update the version in `DistroMetadata.java` with the new release version
    - When updating the OTel Agent/SDK version, update the OTLP version header in `DistroMetadata.java`

1. Update the Changelog

1. If the new release updates the OpenTelemetry SDK and/or agent versions, update the `Latest release built with` section in the [README](./README.md).

1. Once the above changes are merged into `main`, tag `main` with the new version, e.g. `v0.1.1`. Push the tags. This will kick off CI, which will publish a draft GitHub release, and publish to Maven.

1. Update Release Notes on the new draft GitHub release, and publish that.

1. Merge public docs PR and onboard docs PR

Voila!
