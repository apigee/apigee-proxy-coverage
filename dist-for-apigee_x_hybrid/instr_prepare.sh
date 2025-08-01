#!/bin/bash

source ./config_vars

echo "Downloading API Bundle $api revision $rev"
apigeecli apis fetch --name "$API_NAME" --rev "$API_REVISION" --org "$APIGEE_ORG" --token "$TOKEN"

echo "Instrumenting $API_NAME bundle"
java -jar apc-1.0.jar -z "${API_NAME}__${API_REVISION}.zip"

unzip "${API_NAME}__${API_REVISION}_instr.zip"

echo "Deploying Instrumented $API_NAME bundle"
apigeecli apis create bundle -n "${API_NAME}" \
	-f "apiproxy" -e "$APIGEE_ENV" \
	--token "$TOKEN" -o "$PROJECT_ID" \
	--ovr --wait

echo "Deleting  Instrumented KV Data"

curl -X DELETE -H "Authorization: Bearer $TOKEN" \
	"https://apigee.googleapis.com/v1/organizations/$APIGEE_ORG/apis/$API_NAME/keyvaluemaps/instrument"

echo "Note: You can ignore the last error - its trying to delete a key value map named 'instrument' if it exists."
