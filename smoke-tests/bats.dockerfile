FROM bats/bats:latest
RUN apk add --no-cache curl
COPY smoke.bats /code/bats/smoke.bats
WORKDIR /code/bats
CMD [ "." ]
