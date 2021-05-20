# honeycomb-opentelemetry-java changelog

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

