FROM bats/bats:latest
RUN apk add --no-cache curl jq
COPY smoke.bats /code/bats/smoke.bats
WORKDIR /code/bats
# bats entrypoint will execute everything in a directory named in CMD
CMD [ "." ]
