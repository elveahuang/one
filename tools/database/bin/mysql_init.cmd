@echo off

rem 进入工作目录
set ROOT=%~dp0..
echo %ROOT%
cd %ROOT%

rem 初始化环境变量
set MYSQL_PWD=root

rem 初始化数据库

mysql -u root -h 127.0.0.1 -P 3306 --default-character-set=utf8mb4 -v < "%ROOT%/mysql/db_mysql_create_db.sql"
mysql -u root -h 127.0.0.1 -P 3306 --default-character-set=utf8mb4 -v < "%ROOT%/mysql/db_mysql_schema_core.sql"
mysql -u root -h 127.0.0.1 -P 3306 --default-character-set=utf8mb4 -v < "%ROOT%/mysql/db_mysql_schema_quartz.sql"
mysql -u root -h 127.0.0.1 -P 3306 --default-character-set=utf8mb4 -v < "%ROOT%/mysql/db_mysql_schema_webapp.sql"
mysql -u root -h 127.0.0.1 -P 3306 --default-character-set=utf8mb4 -v < "%ROOT%/mysql/db_mysql_data_core.sql"
mysql -u root -h 127.0.0.1 -P 3306 --default-character-set=utf8mb4 -v < "%ROOT%/mysql/db_mysql_data_init.sql"
