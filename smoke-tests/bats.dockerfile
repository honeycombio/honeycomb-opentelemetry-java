FROM bats/bats:latest
RUN apk add --no-cache curl jq
COPY smoke.bats /code/smoke.bats
# bats entrypoint will pass the value of CMD to the bats command
CMD [ "smoke.bats" ]
