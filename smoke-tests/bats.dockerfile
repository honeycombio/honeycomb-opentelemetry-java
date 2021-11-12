FROM bats/bats:latest AS base
RUN apk add --no-cache curl jq
COPY test_helpers /code/test_helpers
COPY *.bats /code/
# bats entrypoint will pass the value of CMD to the bats command

FROM base AS smoke-agent-manual
CMD [ "smoke-agent-manual.bats" ]

FROM base AS smoke-agent-only
CMD [ "smoke-agent-only.bats" ]

FROM base AS smoke-sdk
CMD [ "smoke-sdk.bats" ]
