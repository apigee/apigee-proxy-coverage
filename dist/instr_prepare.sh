#!/bin/bash

source ./config_vars

echo "Downloading API Bundle $api revision $rev"
curl -u $username:$password -o "$api-$rev.zip" "https://api.enterprise.apigee.com/v1/o/$org/apis/$api/revisions/$rev/?format=bundle"

echo "Instrumenting $api bundle"
java -jar apc-1.0.jar -z "$api-$rev.zip"

echo "Deploying Instrumented $api bundle"
./deploy.py -n $api -u $username:$password -o $org  -e $env -z $api-${rev}_instr.zip

echo "Deleting  Instrumented KV Data"
curl -X DELETE -u $username:$password "https://api.enterprise.apigee.com/v1/o/$org/apis/$api/keyvaluemaps/instrument"
echo "Note: You can ignore the last error - its trying to delete a key value map named 'instrument' if it exists."
