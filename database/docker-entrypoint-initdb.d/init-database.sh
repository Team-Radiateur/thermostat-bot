#!/bin/bash

set -xe

mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<EOF
create user if not exists '$MYSQL_USER'@'%' identified by '$MYSQL_PASSWORD';
create database if not exists $MYSQL_DATABASE character set utf8 collate utf8_general_ci;
grant all privileges on $MYSQL_DATABASE.* to '$MYSQL_USER'@'%';
flush privileges;
EOF
