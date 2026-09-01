#!/bin/bash

# 进入工作目录
ROOT=$(cd $(dirname $0);cd ../../; pwd)
cd $ROOT
echo Current workspace : $ROOT

# 编译构建
echo Building...
chmod a+x ./gradlew
./gradlew clean bootJar

# 复制文件
echo Copying files...
cp $ROOT/platform-services/admin-server/build/libs/*.jar $ROOT/tools/deploy/binaries/
cp $ROOT/platform-services/app-server/build/libs/*.jar $ROOT/tools/deploy/binaries/
