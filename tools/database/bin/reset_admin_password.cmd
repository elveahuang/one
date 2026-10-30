@echo off
setlocal enabledelayedexpansion
REM Reset the local development admin password (local / development ONLY).
REM Usage:
REM   tools\database\bin\reset_admin_password.cmd            (reset to default "admin")
REM   tools\database\bin\reset_admin_password.cmd <password>
REM The password is stored as a BCrypt hash ({bcrypt}$2a$...), never plaintext.
REM The script also clears the Redis (db 11) user cache so the change applies at once.
REM Do NOT run this against sit / pro.

set "NEW_PASSWORD=%~1"
if "%NEW_PASSWORD%"=="" set "NEW_PASSWORD=admin"
if "%PG_CONTAINER%"=="" set "PG_CONTAINER=pgsql"
if "%REDIS_CONTAINER%"=="" set "REDIS_CONTAINER=redis"
if "%DB_NAME%"=="" set "DB_NAME=one_platform"
if "%REDIS_PASSWORD%"=="" set "REDIS_PASSWORD=redis"
if "%REDIS_DB%"=="" set "REDIS_DB=11"
if "%APP_USER%"=="" set "APP_USER=admin"

echo [1/3] target: container=%PG_CONTAINER% db=%DB_NAME% user=%APP_USER%

set "CRYPTO_JAR="
for /f "delims=" %%f in ('dir /b /s "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.springframework.security\spring-security-crypto\*.jar" 2^>nul') do (
    echo %%~nxf | findstr /i "sources" >nul || set "CRYPTO_JAR=%%f"
)
set "LOGGING_JAR="
for /f "delims=" %%f in ('dir /b /s "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\commons-logging\commons-logging\*.jar" 2^>nul') do (
    echo %%~nxf | findstr /i "sources" >nul || set "LOGGING_JAR=%%f"
)

if "%CRYPTO_JAR%"=="" (
    echo ERROR: spring-security-crypto jar not found. Run "gradlew build" once first.
    exit /b 1
)
if "%LOGGING_JAR%"=="" (
    echo ERROR: commons-logging jar not found. Run "gradlew build" once first.
    exit /b 1
)

set "TMPD=%TEMP%\zc_genpwd_%RANDOM%"
mkdir "%TMPD%" 2>nul
> "%TMPD%\GenPwd.java" echo import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
>> "%TMPD%\GenPwd.java" echo public class GenPwd {
>> "%TMPD%\GenPwd.java" echo     public static void main(String[] args) {
>> "%TMPD%\GenPwd.java" echo         System.out.print("{bcrypt}" + new BCryptPasswordEncoder().encode(args[0]));
>> "%TMPD%\GenPwd.java" echo     }
>> "%TMPD%\GenPwd.java" echo }

javac -cp "%CRYPTO_JAR%" -d "%TMPD%" "%TMPD%\GenPwd.java" || goto :fail
for /f "delims=" %%h in ('java -cp "%TMPD%;%CRYPTO_JAR%;%LOGGING_JAR%" GenPwd "%NEW_PASSWORD%"') do set "HASH=%%h"

echo !HASH! | findstr /b /c:"{bcrypt}" >nul || (
    echo ERROR: generated hash has an unexpected format.
    goto :fail
)
echo [2/3] bcrypt hash generated

set "SQLFILE=%TMPD%\reset.sql"
> "%SQLFILE%" echo update sys_user set password = '!HASH!', updated_at = now() where username = '%APP_USER%';
docker cp "%SQLFILE%" %PG_CONTAINER%:/tmp/zc_reset_admin.sql >nul || goto :fail
docker exec %PG_CONTAINER% psql -U root -d %DB_NAME% -v ON_ERROR_STOP=1 -f /tmp/zc_reset_admin.sql || goto :fail
echo [3/3] database updated

for /f "delims=" %%k in ('docker exec %REDIS_CONTAINER% redis-cli -a %REDIS_PASSWORD% --no-auth-warning -n %REDIS_DB% --scan --pattern "user:*" 2^>nul') do (
    docker exec %REDIS_CONTAINER% redis-cli -a %REDIS_PASSWORD% --no-auth-warning -n %REDIS_DB% del "%%k" >nul
)
echo [3/3] redis user cache cleared

echo DONE: password for "%APP_USER%" has been reset.
rmdir /s /q "%TMPD%" 2>nul
exit /b 0

:fail
rmdir /s /q "%TMPD%" 2>nul
echo FAILED
exit /b 1
