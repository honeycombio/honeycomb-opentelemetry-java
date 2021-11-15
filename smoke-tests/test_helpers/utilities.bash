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
	NEXT_WAIT_TIME=0
	until [ $NEXT_WAIT_TIME -eq 5 ] || [ "$(wc -l ./collector/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "# Waiting $(( NEXT_WAIT_TIME++ ))s for collector to receive data." >&3
		sleep $NEXT_WAIT_TIME
	done
	[ $NEXT_WAIT_TIME -lt 5 ]
}

wait_for_flush() {
	NEXT_WAIT_TIME=0
	until [ $NEXT_WAIT_TIME -eq 5 ] || [ "$(wc -l ./collector/data.json | awk '{ print $1 }')" -eq 0 ]
	do
		echo "Waiting $(( NEXT_WAIT_TIME++ ))s for collector data flush."
		sleep $NEXT_WAIT_TIME
	done
	[ $NEXT_WAIT_TIME -lt 5 ]
}
