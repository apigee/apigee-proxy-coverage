#!/bin/bash

source ./config_vars

INST_TXT=kvm_instrument.txt

rm -fr "$INST_TXT"

echo "Downloading Recorded Instrumented Data"

curl -H "Authorization: Bearer $TOKEN" \
	"https://apigee.googleapis.com/v1/organizations/$APIGEE_ORG/apis/${API_NAME}/keyvaluemaps/instrument/entries" \
	| jq 'with_entries(if .key == "keyValueEntries" then .key = "entry" else . end)' > $INST_TXT

echo "Generating Coverage report"
java -jar apc-1.0.jar -z "$API_NAME__$API_REVISION.zip" -kv "$INST_TXT" -o "$REPORT_DIR"

open $report_dir/summary.html&
