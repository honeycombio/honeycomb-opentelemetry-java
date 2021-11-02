FROM otel/opentelemetry-collector:latest
COPY otel-collector-config.yaml /etc/otel/config.yaml
COPY --chown=10001:10001 data.json /output/data.json
