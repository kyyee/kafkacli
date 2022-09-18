#!/bin/bash

current_path=$(cd $(dirname $0);pwd)

mvn clean package -Dmaven.test.skip=true -U

ls -al $current_path/target/

cp $current_path/target/app.exe $WORKSPACE/$tar_name_vx-$detail_version/scst-verify-desktop/
cp $current_path/target/app-cli.exe $WORKSPACE/$tar_name_vx-$detail_version/scst-verify-desktop/
cp -rf $current_path/jre $WORKSPACE/$tar_name_vx-$detail_version/scst-verify-desktop/
