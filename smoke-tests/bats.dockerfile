FROM bats/bats:latest
COPY smoke.bats /code/smoke.bats
# CMD ["bats", "smoke.bats"]
