# honeycomb-opentelemetry-java changelog

## [1.7.0] - 2024-04-16

### Enhancements

- feat: return an OpenTelemetrySdk from the builder (#451) | [@NLincoln](https://github.com/NLincoln)

### Maintenance

- maint: bump otel to 1.35/1.33.1 (#453) | [@JamieDanielson](https://github.com/JamieDanielson)
- maint: add labels to release.yml for auto-generated grouping (#450) | [@JamieDanielson](https://github.com/JamieDanielson)

## [1.6.0] - 2024-03-06

### Enhancements

- feat: add support for classic ingest keys (#448) | [@cewkrupa](https://github.com/cewkrupa)

### Maintenance
- maint: update codeowners to pipeline-team (#446) | [@JamieDanielson](https://github.com/JamieDanielson)
- maint: update codeowners to pipeline (#445) | [@JamieDanielson](https://github.com/JamieDanielson)

## [1.5.2] - 2023-06-21

### Maintenance

- maint(deps): bump versions.opentelemetry from 1.26.0 to 1.27.0 (#428)
- maint(deps): bump versions.opentelemetryJavaagent from 1.26.0 to 1.27.0 (#431)
- maint(deps): bump org.junit.jupiter:junit-jupiter-api from 5.9.2 to 5.9.3 (#429)
- maint(deps): bump io.grpc:grpc-netty-shaded from 1.55.1 to 1.56.0 (#430)
- maint: update licenses for new otel-java versions (#432)| [@JamieDanielson](https://github.com/JamieDanielson)

## [1.5.1] - 2023-06-06

### Maintenance

- maint: update docker image and collector version in smoke tests (#422) | @JamieDanielson
- maint(deps): bump io.grpc:grpc-netty-shaded from 1.53.0 to 1.55.1 (#423) | @dependabot
- maint(deps): bump versions.opentelemetry from 1.25.0 to 1.26.0 (#425) | @dependabot
- maint(deps): bump versions.opentelemetryJavaagent from 1.25.0 to 1.26.0 (#424) | @dependabot
- maint(deps): bump org.junit:junit-bom from 5.9.2 to 5.9.3 (#420) | @dependabot

## [1.5.0] - 2023-04-28

### Maintenance

- maint(deps): bump org.junit.jupiter:junit-jupiter-engine from 5.9.2 to 5.9.3 (#416)
- maint(deps): bump versions.opentelemetry from 1.24.0 to 1.25.0 (#415)
- maint(deps): bump versions.opentelemetryJavaagent from 1.23.0 to 1.25.0 (#413)
- maint(deps): bump versions.opentelemetry from 1.23.1 to 1.24.0 (#408)
- maint(deps): bump io.github.gradle-nexus.publish-plugin from 1.2.0 to 1.3.0 (#407)
- maint(deps): bump io.github.gradle-nexus.publish-plugin from 1.1.0 to 1.2.0 (#402)
- maint(deps): upgrade otel java agent to 1.23.0 (#406)
- maint(deps): bump versions.opentelemetry from 1.22.0 to 1.23.1 (#401)
- maint: Add dependency license files (#403) | [@pkanal](https://github.com/pkanal)
- maint: update licenses in prep for release (#419) | [@JamieDanielson](https://github.com/JamieDanielson)

## [1.4.2] - 2023-02-16

### Maintenance

- maint: add smoke tests for grpc/http exporter protocols for agent only (#391) | @JamieDanielson
- maint(deps): bump otel to 1.22.1 (#393) | @vreynolds
- maint(deps): bump junit-jupiter-engine from 5.9.1 to 5.9.2 (#390)
- maint(deps): bump io.grpc:grpc-netty-shaded from 1.50.2 to 1.53.0 (#395)
- maint(deps): bump junit-bom from 5.9.1 to 5.9.2 (#386)

## [1.4.1] - 2023-01-17

### Fixes

- fix: exception when initialized without API key (#378) | @vreynolds

### Maintenance

- maint(deps): bump mockito-core from 4.6.1 to 4.11.0 (#377)
- maint(deps): bump junit-jupiter-api from 5.8.2 to 5.9.2 (#380)
- Bump junit-bom from 5.9.0 to 5.9.1 (#359)
- maint: additional metadata to diff sdk from agent (#379) | @vreynolds
- chore: Dependabot fixes (#371) | @kentquirk
- ci: update validate PR title workflow (#364) | @pkanal
- ci: fix slack notify channel (#363) | @vreynolds
- ci: validate PR title (#362) | @pkanal
- maint: don't run extra jobs when releasing (#355) | @vreynolds

## [1.4.0] - 2022-10-28

### Maintenance

- Update Otel SDK and JavaAgent to v1.19.0/v1.19.1 (#348) | @MikeGoldsmith
- maint: upgrade gradle to 7.5 (#351) | @vreynolds
- maint: include generated release.yml in published artifacts (#326) | @robbkidd
- maint: add un-versioned copy of agent to Github assets (#323) | @JamieDanielson
- Bump examples/org.springframework.boot from 2.7.0 to 2.7.5 (#322, #352)
- Bump examples/io.spring.dependency-management from 1.0.11.RELEASE to 1.1.0 (#353)
- Bump junit-jupiter-engine from 5.8.2 to 5.9.1 (#345)
- Bump grpc-netty-shaded from 1.47.0 to 1.50.2 (#350)
- Bump junit-bom from 5.8.2 to 5.9.0 (#329)

## [1.3.0] - 2022-07-01

### Enhancements

- Produce a JSON of release info on build (#319) | [@robbkidd](https://github.com/robbkidd)
- Decorate existing Sampler with DeterministicTraceSampler instead of replacing it (#316) | [@GrahamLea](https://github.com/GrahamLea)

### Maintenance

- Move workflows into workflows directory (#318) | [@JamieDanielson](https://github.com/JamieDanielson)
- Add github workflow to auto-create issue for public docs (#314) | [@JamieDanielson](https://github.com/JamieDanielson)
- Include onboard docs in releasing, move to top (#313) | [@JamieDanielson](https://github.com/JamieDanielson)

## [1.2.0] - 2022-06-21

### Added

- Add OTLP version to headers (HTTP & gRPC) (#300) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)

### Fixes

- Fixed False Warning about no SERVICE_NAME defined when OTEL_SERVICE_NAME was present. (#310) | | [@ArtificialBlue](https://github.com/ArtificialBlue)

### Maintenance

- Upgrade otel versions to 1.14.0 (#307) | [@JamieDanielson](https://github.com/JamieDanielson)
- Bump grpc-netty-shaded from 1.43.1 to 1.47.0 (#305)
- Bump org.springframework.boot from 2.6.6 to 2.7.0 (#301)
- Bump mockito-junit-jupiter from 4.4.0 to 4.6.1 (#309)
- Bump mockito-core from 4.6.0 to 4.6.1 (#308)
- Bump mockito-inline from 4.5.1 to 4.6.1 (#304)
- Bump mockito-core from 4.5.1 to 4.6.0 (#302)
- Bump mockito-inline from 4.4.0 to 4.5.1 (#299)
- Bump guava from 31.0.1-jre to 31.1-jre (#268)
- Bump mockito-core from 4.4.0 to 4.5.1 (#290)

## [1.1.1] - 2022-05-11

### Fixes

- Append traces path (v1/traces) to endpoint if using http/protobuf with agent (#296) | [@JamieDanielson](https://github.com/JamieDanielson)

## [1.1.0] - 2022-04-25

### Enhancements

- Adds support for SDK to set OTLP protocol (#283) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)

### Maintenance

- Bump mockito-inline from 4.2.0 to 4.4.0 (#286) | [dependabot](https://github.com/dependabot)
- Bump mockito-junit-jupiter from 4.2.0 to 4.4.0 (#276) | [dependabot](https://github.com/dependabot)
- Bump mockito-core from 4.2.0 to 4.4.0 (#275) | [dependabot](https://github.com/dependabot)
- Bump org.springframework.boot from 2.6.2 to 2.6.6 (#281) | [dependabot](https://github.com/dependabot)
- resolve deprecations from upstream (#278) | [@robbkidd](https://github.com/robbkidd)

## [1.0.0] - 2022-03-23

### This project is now out of BETA \o/

### Maintenance

- bump otel version from 1.11 to 1.12.0 (#273) | [@vreynolds](https://github.com/vreynolds)

## [0.10.0] - 2022-02-09

### Maintenance

- Update OpenTelemetry SDK & Agent dependencies (#264) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)
- Add maven badge to README (#261) | [@vreynolds](https://github.com/vreynolds)

## [0.9.0] - 2022-02-09

### Enhancements

- Provide more feedback to users to help configure for use in E&S (#255) | [@JamieDanielson](https://github.com/JamieDanielson)

### Maintenance

- Publish snapshots (#248) | [@vreynolds](https://github.com/vreynolds)

## [0.8.1] - 2022-01-11

### Changed

- Create the AttributeKey for SampleRate once (#240) | [@mveitas](https://github.com/mveitas)

### Maintenance

- Bump opentelemetryJavaagent to 1.9.2 to address memory leak in reactor-netty (#245) | [@mveitas](https://github.com/mveitas)
- Bump org.springframework.boot from 2.6.1 to 2.6.2 (#242) | [dependabot](https://github.com/dependabot)
- Bump com.github.johnrengelman.shadow from 7.1.1 to 7.1.2 (#241) | [dependabot](https://github.com/dependabot)
- Bump grpc-netty-shaded from 1.41.0 to 1.43.1 (#239) | [dependabot](https://github.com/dependabot)
- gh: add re-triage workflow (#237) | [@vreynolds](https://github.com/vreynolds)
- update smoke tests (#238) | [@vreynolds](https://github.com/vreynolds)

## [0.8.0] - 2021-12-23

### Fixes

- remove parent-based sampler, sample with trace ID ratio only (#235) | [@vreynolds](https://github.com/vreynolds)

### Maintenance

- Bump com.github.johnrengelman.shadow from 7.1.0 to 7.1.1 (#231) | [dependabot](https://github.com/dependabot)
- Bump opentelemetry from 1.7.1 to 1.9.1 (#218) | [dependabot](https://github.com/dependabot)
- Bump junit-jupiter-engine from 5.8.1 to 5.8.2 (#232) | [dependabot](https://github.com/dependabot)
- Bump guava from 30.1.1-jre to 31.0.1-jre (#164) | [dependabot](https://github.com/dependabot)
- Bump mockito-inline from 4.1.0 to 4.2.0 (#230) | [dependabot](https://github.com/dependabot)
- Bump mockito-core from 4.1.0 to 4.2.0 (#229) | [dependabot](https://github.com/dependabot)
- Bump mockito-junit-jupiter from 4.1.0 to 4.2.0 (#228) | [dependabot](https://github.com/dependabot)
- Bump junit-bom from 5.8.1 to 5.8.2 (#226) | [dependabot](https://github.com/dependabot)
- Bump org.springframework.boot from 2.6.0 to 2.6.1 (#222) | [dependabot](https://github.com/dependabot)
- maint: Make smarter smoke tests (#225) | [@JamieDanielson](https://github.com/JamieDanielson)
- Bump junit-jupiter-api from 5.8.1 to 5.8.2 (#219) | [dependabot](https://github.com/dependabot)
- Update dependabot to monthly (#215) | [@vreynolds](https://github.com/vreynolds)

## [0.7.0] - 2021-11-24

### !!! Potential Breaking Change !!!

- Remove -all suffix from artifacts (#214) | [@JamieDanielson](https://github.com/JamieDanielson)

The java agent no longer has an `-all` suffix, ensure dependencies are updated accordingly.

### Changes

- Update DeterministicSampler to use OTel core Parent/TraceIDRatio samplers (#209) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)
- Add support for properties files (#202) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)
- update otel sdk and agent (#200) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)
- when setting attributes return Builder (#192) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)

### Maintenance

- Update SDK tests to validate returned OpenTelemtry instance (#210)
- Backfill unit tests (#205)
- Add Smoke test for JVM metric being emitted (#206)
- add smoke test for baggage span processor (#203)
- Smoke tests for SDK-only traces (& duplication reduction) (#197)
- teach gradle/make to display the project version (#199)
- Add tests for agent+manual instrumentation (#194)
- Simplify example output (#193)
- a BATS-based smoke-test (#186)
- CI: align Maven and GitHub releases (#179)
- empower apply-labels action to apply labels (#187)
- Bump com.github.johnrengelman.shadow from 7.0.0 to 7.1.0 (#163)
- Bump org.springframework.boot from 2.5.5 to 2.6.0 (#207)
- Bump mockito-core from 3.12.4 to 4.1.0 (#208)
- Bump org.springframework.boot from 2.5.4 to 2.5.5 (#152)
- Bump junit-jupiter-api from 5.7.2 to 5.8.1 (#153)

## [0.6.1] - 2021-10-14

### Fixes

- Fixing package associated with auto-configuration for HoneycombSdkTracerProviderConfigurer (#176) | [@mveitas](https://github.com/mveitas)

## [0.6.0] - 2021-10-08

### Enhancements

- Always add baggage processor to SDK processors (#169) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)

### Fixes

- Remove google guava dep that suppressed gRPC (#162) | [@JamieDanielson](https://github.com/JamieDanielson)
- re-add java runtime version distro metadata (#160) | [@vreynolds](https://github.com/vreynolds)

### Maintenance

- Clear the way; docs are in docs now (#171) | [@JamieDanielson](https://github.com/JamieDanielson)
- Update notes around SDK and gRPC usage (#166) | [@JamieDanielson](https://github.com/JamieDanielson)
- Add example app for testing agent-only usage (#165) | [@JamieDanielson](https://github.com/JamieDanielson)
- add Java 17 to test matrix (#156) | [@vreynolds](https://github.com/vreynolds)
- sonatype release is automated (#155) | [@vreynolds](https://github.com/vreynolds)

## [0.5.0] - 2021-09-27

### !!! Breaking Changes !!!

- Replace HoneycombSdk with OpenTelemetryConfiguration.Builder (#132)

### Added

- Support for enabling OTel metrics (#135)
- `telemetry.sdk.*` resource attributes in the SDK (#139)

### Removed

- `honeycomb.distro.language` and `honeycomb.distro.runtime_version` resource attributes (#148)

### Maintenance

- Add javadoc for method param (#145)
- Automate nexus publish/close/release (#136)
- Adds example apps (#124, #137)
- docs: agent debug (#138)
- docs: add OTEL version to readme (#126)
- Add NOTICE (#123)
- Link to public dosc for SDK setup (#121)
- Add Stalebot (#119, #122, #125)
- Update docs around SDK use. (#120)
- Spruce up CI (#106)
- Update link (#100)
- Add issue and PR templates (#98)
- Bump OTEL to 1.6.0 (#134, #146, #149)
- Bump junit-jupiter-engine from 5.7.2 to 5.8.1 (#129, #142)
- Bump junit-bom from 5.7.1 to 5.8.1 (#86, #143)
- Bump grpc-netty-shaded from 1.39.0 to 1.41.0 (#103, #144)
- Bump mockito-core from 3.11.2 to 3.12.4 (#102, #118)

## [0.4.0] - 2021-08-12

### Changes

- Disabled metrics from the agent by default (#92) (see #96 for the plan to add them back in) | [@JamieDanielson](https://github.com/JamieDanielson)
- Make API Key and Dataset optional (#89) | [@vreynolds](https://github.com/vreynolds)
- Add deterministic sampler tests (#73) | [@MikeGoldsmith](https://github.com/MikeGoldsmith)

### Dependency Updates

- Remove unused dependency: autoservice (#88) | [@vreynolds](https://github.com/vreynolds)
- Bump com.github.johnrengelman.shadow from 6.0.0 to 7.0.0 (#75) | @dependabot
- Bump junit-jupiter-engine from 5.7.1 to 5.7.2 (#57) | @dependabot
- Bump junit-jupiter-api from 5.7.1 to 5.7.2 (#77) | @dependabot
- Bump grpc-netty-shaded from 1.37.0 to 1.39.0 (#80) | @dependabot
- Bump mockito-core from 3.8.0 to 3.11.2 (#76) | @dependabot
- Bump guava from 30.1-jre to 30.1.1-jre (#55) | @dependabot
- Bump auto-common from 0.8 to 1.0.1 (#70) | @dependabot
- Bump versions.opentelemetry from 1.0.1 to 1.2.0 (#58) | @dependabot

### Maintenance

- Add OSS lifecycle badge (#95) | [@vreynolds](https://github.com/vreynolds)
- Add community health files | [@vreynolds](https://github.com/vreynolds)
- Updates Github Action Workflows (#81) | [@bdarfler](https://github.com/bdarfler)
- Updates Dependabot Config (#79) | [@bdarfler](https://github.com/bdarfler)
- Switches CODEOWNERS to telemetry-team (#78) | [@bdarfler](https://github.com/bdarfler)

## [0.3.0] - 2021-06-02

### Added

- Add opentelemetry-extension-annotations as API dependency in the sdk (#69) | [@vreynolds](https://github.com/vreynolds)

## [0.2.1] - 2021-05-25

### Fixed

- Add POM descriptions to pass Nexus validation (#65) | [@vreynolds](https://github.com/vreynolds)

## [0.2.0] - 2021-05-20

### Added

- Add baggage propagator to the SDK (#60) | [@vreynolds](https://github.com/vreynolds)
- Add SDK builder method for passing in BaggageSpanProcessor or any custom SpanProcessor (#48, #52) | [@shelbyspees](https://github.com/shelbyspees)
- Create methods for adding arbitrary resource attributes (#53) | [@paulosman](https://github.com/paulosman)

### Maintenance

- DRY up constants between modules (#50) | [@vreynolds](https://github.com/vreynolds)
- Add maven publish (#45) | [@vreynolds](https://github.com/vreynolds)
- Start drafting docs to go on the docs site (#41) | [@shelbyspees](https://github.com/shelbyspees)
- Fix readme sampler usage (#39) | [@vreynolds](https://github.com/vreynolds)

## [0.1.1] - 2021-04-29

- Fix: allow users to set OTel resource attributes when using the javaagent (#40, #42)

## [0.1.0] - 2021-04-28

### Added

- Initial preview release of Honeycomb's OpenTelemetry distribution for Java!

