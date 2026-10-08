#!/bin/bash

# 进入工作目录
ROOT="$(cd $(dirname $0);cd ../; pwd)"
echo $ROOT
cd $ROOT

# 初始化环境变量
export PGPASSWORD=root
export PGCLIENTENCODING=UTF8

# 初始化数据库

psql -U root -d test -h 127.0.0.1 -p 5432 -f "$ROOT/pgsql/db_pgsql_create_db.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "$ROOT/pgsql/db_pgsql_schema_core.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "$ROOT/pgsql/db_pgsql_schema_quartz.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "$ROOT/pgsql/db_pgsql_schema_webapp.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "$ROOT/pgsql/db_pgsql_data_core.sql"
psql -U root -d test -h 127.0.0.1 -p 5432 -f "$ROOT/pgsql/db_pgsql_data_init.sql"
