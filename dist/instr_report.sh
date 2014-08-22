#!/bin/bash

source ./config_vars

inst_txt=kvm_instrument.txt

rm -fr $inst_txt

echo "Downloading Recorded Instrumented Data"
curl -u $username:$password -o $inst_txt "https://api.enterprise.apigee.com/v1/o/$org/apis/$api/keyvaluemaps/instrument"

echo "Generating Coverage report"
java -jar apc-1.0.jar -z "$api-$rev.zip" -kv $inst_txt -o $report_dir

open $report_dir/summary.html&
