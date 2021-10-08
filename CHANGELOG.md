# honeycomb-opentelemetry-java changelog

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

