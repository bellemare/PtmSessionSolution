#!/bin/bash

curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt -y

#Copy the sample log over to Cloudera's HDFS. Local mode was being a jerk and I didn't care to debug their VM's particularities.
hadoop fs -copyFromLocal ../src/main/resources/2015_07_22_mktplace_shop_web_log_sample.log.gz /user/
