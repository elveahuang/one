@echo off

rem 进入工作目录
set ROOT=%~dp0..
echo %ROOT%
cd %ROOT%

rem 初始化环境变量
set PGPASSWORD=root
set PGCLIENTENCODING=UTF8

rem 初始化数据库

psql -U root -d test -h 127.0.0.1 -p 5432 -f "%ROOT%/pgsql/db_pgsql_create_db.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "%ROOT%/pgsql/db_pgsql_schema_core.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "%ROOT%/pgsql/db_pgsql_schema_quartz.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "%ROOT%/pgsql/db_pgsql_schema_webapp.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "%ROOT%/pgsql/db_pgsql_data_core.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "%ROOT%/pgsql/db_pgsql_data_init.sql"
