#!/bin/bash

set -xe

mariadb -u root -p"$MYSQL_ROOT_PASSWORD" <<EOF
set global character_set_results = utf8mb4;
create user if not exists '$MYSQL_USER'@'%' identified by '$MYSQL_PASSWORD';
create database if not exists $MYSQL_DATABASE default character set utf8mb4 collate utf8mb4_unicode_ci;
grant all privileges on $MYSQL_DATABASE.* to '$MYSQL_USER'@'%';
flush privileges;
EOF
