#!/bin/bash

source ./config_vars

echo "Downloading API Bundle $API_NAME revision $API_REVISION"
apigeecli apis fetch --name "$API_NAME" --rev "$API_REVISION" --org "$APIGEE_ORG" --token "$TOKEN"

echo "Instrumenting $API_NAME bundle"
java -jar apc-1.0.jar -z "${API_NAME}__${API_REVISION}.zip"

unzip "${API_NAME}__${API_REVISION}_instr.zip"

echo "Deploying Instrumented $API_NAME bundle"
apigeecli apis create bundle --name "${API_NAME}" \
	--proxy-folder "apiproxy" --env "$APIGEE_ENV" \
	--token "$TOKEN" --org "$APIGEE_ORG" \
	--ovr --wait

echo "Deleting  Instrumented KV Data"

apigeecli kvms delete --proxy $API_NAME --name instrument --org $APIGEE_ORG --token $TOKEN

echo "Note: You can ignore the last error - its trying to delete a key value map named 'instrument' if it exists."
