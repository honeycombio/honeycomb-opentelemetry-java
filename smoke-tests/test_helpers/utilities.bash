# UTILITY FUNCS

spans_from_library_named() {
	jq ".resourceSpans[] |
			.instrumentationLibrarySpans[] |
			select(.instrumentationLibrary.name == \"$1\").spans[]" \
		./collector/data.json
}
# test span name
span_names_for() {
	spans_from_library_named $1 | jq '.name'
}

# test span attributes
span_attributes_for() {
	# $1 - library name

	spans_from_library_named $1 | \
		jq ".attributes[]"
}

wait_for_data() {
	echo -n "# ⏳ Waiting for collector to receive data" >&3
	NEXT_WAIT_TIME=0
	until [ $NEXT_WAIT_TIME -eq 5 ] || [ "$(wc -l ./collector/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo -n " ... $(( NEXT_WAIT_TIME++ ))s" >&3
		sleep $NEXT_WAIT_TIME
	done
	echo "" >&3
	[ $NEXT_WAIT_TIME -lt 5 ]
}

wait_for_flush() {
	echo -n "# ⏳ Waiting for collector data flush" >&3
	NEXT_WAIT_TIME=0
	until [ $NEXT_WAIT_TIME -eq 5 ] || [ "$(wc -l ./collector/data.json | awk '{ print $1 }')" -eq 0 ]
	do
		echo -n " ... $(( NEXT_WAIT_TIME++ ))s" >&3
		sleep $NEXT_WAIT_TIME
	done
	echo "" >&3
	[ $NEXT_WAIT_TIME -lt 5 ]
}

# Wait loop for one of our example Java apps to be started and ready to receive traffic.
#
# Arguments:
#   $1 - the name of the container/service in which the app is running
wait_for_ready_app() {
	CONTAINER=${1:?container name is a required parameter}
	MAX_RETRIES=5
	echo -n "# 🍿 Setting up ${CONTAINER}" >&3
	NEXT_WAIT_TIME=0
	until [ $NEXT_WAIT_TIME -eq $MAX_RETRIES ] || [[ $(docker-compose logs ${CONTAINER} | grep "OK I'm ready now") ]]
	do
		echo -n " ... $(( NEXT_WAIT_TIME++ ))s" >&3
		sleep $NEXT_WAIT_TIME
	done
	echo "" >&3
	[ $NEXT_WAIT_TIME -lt $MAX_RETRIES ]
}

# Fail and display details if the expected and actual values do not
# equal. Details include both values.
#
# Lifted and then drastically simplified from bats-assert * bats-support
assert_equal() {
	if [[ $1 != "$2" ]]; then
		{
			echo
			echo "-- 💥 values are not equal 💥 --"
			echo "expected : $2"
			echo "actual   : $1"
			echo "--"
			echo
		} >&2 # output error to STDERR
		return 1
	fi
}
