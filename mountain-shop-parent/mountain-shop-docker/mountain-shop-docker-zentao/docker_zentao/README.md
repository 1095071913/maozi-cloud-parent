# docker-zentao

禅道Docker Image File

数据库用户名：root,默认密码：123456。运行时，可以设置MYSQL_ROOT_PASSWORD变量来更改密码。

可挂载目录
/app/zentaopms:该目录为禅道目录，里面包含禅道代码及附件上传目录。
/var/lib/mysql:该目录为数据库的数据目录。

首次运行容器后，浏览器访问http://IP:端口，如果界面显示禅道安装界面，说明容器运行正常。
依照安装程序，最后将禅道安装成功，就可以使用禅道了。
