#!/bin/bash

set -e

cd "${0%/*}"

timeout=$1

jar_path="../target/scala-2.10/PtmSessionSolution-assembly-20161127.0.jar"
data_path="../src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz"

spark-submit \
  --class SessionAppCLI \
  --conf spark.executor.memory=2g \
  $jar_path -i $data_path -t $timeout
  
