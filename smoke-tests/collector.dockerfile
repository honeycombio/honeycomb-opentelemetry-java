FROM otel/opentelemetry-collector:latest
COPY otel-collector-config.yaml /etc/otel/config.yaml
ADD --chown=10001:10001 data.json /var/lib/data.json
