@echo off

rem 进入工作目录
set ROOT=%~dp0..\..\
cd %ROOT%
echo Current workspace : %ROOT%

rem 编译构建
echo Building...
chcp 65001
call .\gradlew.bat clean bootJar

rem 复制文件
echo Copying files...
copy /y %ROOT%\platform-services\admin-server\build\libs\*.jar %ROOT%\tools\deploy\binaries\
copy /y %ROOT%\platform-services\app-server\build\libs\*.jar %ROOT%\tools\deploy\binaries\
