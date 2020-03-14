#!/usr/bin/env bash

[ $DEBUG ] && set -x

if [ "`ls -A /app/zentaopms`" = "" ]; then
  cp -a /var/www/zentaopms/* /app/zentaopms
fi

if [ "`cat /app/zentaopms/VERSION`" != "`cat /var/www/zentaopms/VERSION`" ]; then
  cp -a /var/www/zentaopms/* /app/zentaopms
fi

chmod -R 777 /app/zentaopms/www/data
chmod -R 777 /app/zentaopms/tmp
chmod 777 /app/zentaopms/www
chmod 777 /app/zentaopms/config
chmod -R a+rx /app/zentaopms/bin/*

/etc/init.d/apache2 start

chown -R www-data:www-data /app/zentaopms
chown -R mysql:mysql /var/lib/mysql/
if [ "`ls -A /var/lib/mysql/`" = "" ]; then
  mysql_install_db --defaults-file=/etc/mysql/my.cnf
  /etc/init.d/mysql start
  /usr/bin/mysqladmin -uroot password $MYSQL_ROOT_PASSWORD
  mysql -uroot -e "UPDATE mysql.user SET plugin='mysql_native_password' WHERE user='root';FLUSH PRIVILEGES;"
else
  /etc/init.d/mysql start
fi

tail -f /var/log/apache2/zentao_error_log
