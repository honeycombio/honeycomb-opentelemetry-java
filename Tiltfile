docker_compose("./smoke-tests/docker-compose.yml")
dc_resource("collector", labels="smoke")
dc_resource("app", labels="smoke")
local_resource('run smoke tests', cmd='cd smoke-tests && bats ./smoke.bats', deps=['collector', 'app'], auto_init=False, trigger_mode=TRIGGER_MODE_MANUAL, labels="smoke")
