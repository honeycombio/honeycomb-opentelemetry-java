FROM otel/opentelemetry-collector:latest
COPY otel-collector-config.yaml /etc/otel/config.yaml
# collector image sets USER 10001
ADD --chown=10001:10001 data.json /var/lib/data.json
