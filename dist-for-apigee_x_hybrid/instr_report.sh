#!/bin/bash

source ./config_vars

INST_TXT=kvm_instrument.txt

rm -fr "$INST_TXT"

echo "Downloading Recorded Instrumented Data"

apigeecli kvms entries list --proxy "$API_NAME" --map instrument \
	--org "$APIGEE_ORG" --token "$TOKEN" \
	| jq 'with_entries(if .key == "keyValueEntries" then .key = "entry" else . end)' > $INST_TXT

echo "Generating Coverage report"
java -jar apc-1.0.jar -z "${API_NAME}__${API_REVISION}.zip" -kv "$INST_TXT" -o "$REPORT_DIR"

open $REPORT_DIR/summary.html&
