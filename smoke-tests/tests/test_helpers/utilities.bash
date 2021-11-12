# UTILITY FUNCS

spans_from_library_named() {
	jq ".resourceSpans[] |
			.instrumentationLibrarySpans[] |
			select(.instrumentationLibrary.name == \"$1\").spans[]" \
		/var/lib/data.json
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
	until [ "$(wc -l /var/lib/data.json | awk '{ print $1 }')" -ne 0 ]
	do
		echo "# Waiting for collector to receive data." >&3
		sleep 1
	done
}
