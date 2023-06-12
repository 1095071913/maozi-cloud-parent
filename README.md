# 脚手架说明

该脚手架基于微服务开发，给开发人员快速投入业务开发，封装好了一系列的组件入：日志收集、业务通用方法、框架配置、数据包结果集、监控、常用的第三方接口 等一系列功能，后续会不断升级 框架组件、封装更多通用方法或框架组件、第三方接口、接入Kubesphere Istio

已有三家公司在用的脚手架，每次更新发版都会经过集成的框架基本可用 压测，请放心使用，有问题请咨询QQ1095071913

# 架构图

<img src="https://camo.githubusercontent.com/2a8d393174da1eb4943ebb7c277f6626499130ba06cc251870589a841da22f2e/68747470733a2f2f696d672e616c6963646e2e636f6d2f696d6765787472612f69322f4f31434e30314a386d585568323346596d67327558646c5f2121363030303030303030373232362d302d7470732d323835382d313238322e6a7067" />

<br/>

# 目录说明

```text

maozi-cloud-common
  maozi-cloud-common-tool                 (工具)
  maozi-cloud-common-generate-code        (代码生成工具)
  
  
  
maozi-cloud-config
  maozi-cloud-config-apollo               (Apollo配置)
  maozi-cloud-config-arthas               (Arthas配置)
  maozi-cloud-config-bootadmin            (SpringBootAdmin配置)
  maozi-cloud-config-db                   (数据库配置)
  maozi-cloud-config-discovery            (注册中心配置)
  maozi-cloud-config-dubbo                (Dubbo配置)
  maozi-cloud-config-feign                (Feign配置)
  maozi-cloud-config-job                  (XXL-Job配置)
  maozi-cloud-config-log                  (日志配置)
  maozi-cloud-config-mvc                  (Controller配置)
  maozi-cloud-config-redis                (Redis配置)
  maozi-cloud-config-seata                (Seata配置)
  maozi-cloud-config-sentinel             (Sentinel配置)
  maozi-cloud-config-sentry               (Sentry配置)
  maozi-cloud-config-sso                  (单点登录权限配置)
  maozi-cloud-config-stream               (MQ Stream配置)
  maozi-cloud-config-swagger              (接口文档配置)
  
  
  
maozi-cloud-entity
  maozi-cloud-base-entity
    maozi-cloud-base-do                   (Domain Object 基层)
    maozi-cloud-base-dto                  (Data Transfer Object 基层)
    maozi-cloud-base-enum                 (枚举 基层)
    maozi-cloud-base-vo                   (View Object 基层)
  maozi-cloud-business-entity             
    maozi-cloud-do                        (Domain Object 聚合依赖)
      maozi-cloud-seata-do                (服务通用 Seata Domain)  
    maozi-cloud-dto                       (Data Transfer Object 聚合依赖)
      maozi-cloud-sso-oauth-dto           (授权服务 Data Transfer Object)
      maozi-cloud-system-user-dto         (系统用户服务 Data Transfer Object)
    maozi-cloud-enum                      (枚举 聚合依赖)
    maozi-cloud-result                    (结果集 基层)
    maozi-cloud-vo                        (View Object 聚合依赖)
  
  
  
maozi-cloud-service
  maozi-cloud-service-base
    maozi-cloud-service-base-api          (服务接口 基层)
    maozi-cloud-service-base-impl         (服务接口实现 基层)
    maozi-cloud-service-base-run          (启动服务 基层)
  maozi-cloud-service-db-impl             (服务聚合依赖 数据库基层版)
  maozi-cloud-service-business
    maozi-cloud-service-impl              (服务聚合依赖 基层)
      maozi-cloud-service-bd              (百度地图服务接口实现)
      maozi-cloud-service-qny             (七牛云服务接口实现)
      maozi-cloud-service-ss              (闪送服务接口实现)
      maozi-cloud-service-company-wechat  (企业微信服务接口实现)
    maozi-cloud-service-rest-api          (服务HTTP接口聚合依赖 基层)
    maozi-cloud-service-rpc-api           (服务RPC接口聚合依赖 基层)
  maozi-cloud-service-plugin
    maozi-cloud-service-db-impl           (数据库服务实现 插件基层)
  
  
  
```

<br/>

<br/>

# 集成框架版本说明

|框架|版本|说明|
|:--:|:--:|:--:|
|JDK|17|兼容JDK8，由于Springboot3只支持17所以在Springboot2.6中先用|
|Spring Boot|2.6.11|基于Spring框架的快速开发应用程序框架|
|Spring Cloud|2021.0.4|Netfix推出微服务一站式服务框架|
|Spring Cloud Alibaba|2021.0.4.0|阿里巴巴推出微服务一站式服务框架|
|Spring Cloud Alibaba Dubbo|3.0.8|基于Triple Stream协议 对内 服务与服务之间RPC通信框架|
|Spring Cloud Feign|跟随SC版本|基于HTTP协议 对外 服务与第三方之间RPC通信框架|
|Spring Cloud Alibaba Gateway|跟随SCA版本|黑白名单，鉴权，流量分发 流式处理网关框架|
|Spring Cloud Alibaba Stream Rocketmq|跟随SCA版本|异步消峰消息框架|
|Spring Cloud Alibaba Seata|跟随SCA版本|分布式事务框架|
|Spring Cloud Alibaba Sentinel|跟随SCA版本|监控流量 限流 框架，个人二开支持数据持久化，限流规则持久化Nacos|
|Spring Cloud Alibaba Nacos|跟随SCA版本|分布式服务治理 配置中心 框架|
|Spring Boot Admin|2.6.9|基于Actuator分布式JVM监控框架|
|Spring Boot Oauth2|跟随SC版本|基于Oauth2协议授权服务器授权单点登录 权限 框架|
|Arthas Tunnel|3.6.7|远程进入服务JVM内部监控诊断|
|Skywalking|8.4.0|接口链路跟踪性能分析|
|Mybatis Plus Join|1.2.4|基于Mybatis Plus二开 关系型数据库对象关系映射框架 支持连表查询|
|Mybatis Plus Actable|1.5.0.RELEASE|关系型数据库对象关系映射 正向生成自动建表框架|
|Spring Flyway|跟随SB版本|初始化数据库数据|
|Knife4j|4.0.0|基于Swagger二开 接口文档可视化框架 个人二开新增枚举解析|
|XXL Job|2.3.0|定时任务框架|

<br/>

<br/>

# 编译前准备

<br/>

## 编译前说明

<br/>

**项目不要缺少.git文件 ，不然会编译不过**

编译前要准备Nexus，既然是微服务那就要做远程仓库方便后续打包上线，如果只是想本地测试没有Nexus的话就不要走Maven Deploy编译

我们将会基于Docker来去安装中间件，本地测试的可以考虑安装一个Docker Desktop来进行测试使用，将开始逐步安装

Docker Desktop自带Docker Compose，安装K8s也非常方便简单

<br/>

<br/>

## Windows11安装Docker Desktop

<br/>

### 1. 安装WSL

```sh

wsl --install

# 若报错提示 “无法解析服务器的名称或地址”，则输入
# dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
# dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
# 还是一样则需要在网络和共享设置里，选择当前连接，设置IPv4的属性，更改DNS服务器 114.114.114.114
# 完成后再执行 
# wsl --update
# wsl --install

```

<br/>

### 2. 安装Docker Desktop

游览器下载安装包 https://docs.docker.com/desktop/release-notes/#4163

<br/>

### 3. 设置加速镜像仓库

安装完后打开面板点击登录旁边的setting设置，点击Docker Engine 输入

```json
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  },
  "experimental": false,
  "features": {
    "buildkit": true
  },
  "registry-mirrors": [
    "https://pn1nqbsb.mirror.aliyuncs.com",
    "https://docker.mirrors.ustc.edu.cn",
    "https://kubernetes.github.io"
  ]
}
```

<br/>

<br/>

<br/>

## Docker Compose安装Nexus

<br/>

### 1. 下载[docker-compose.yml](https://gitee.com/xmaozi/maozi-cloud-parent-script/blob/release/nexus/docker-compose.yml)文件

<br/>

### 2. 执行

```sh
docker-compose up -d
```

<br/>

<br/>

## 设置Maven Setting.xml文件

下载[MavenSetting](https://gitee.com/xmaozi/maozi-cloud-parent-script/blob/release/nexus/maven-config-setting.xml)与你本地文件合并到一起，将文件中的  **账号、密码、Nexus地址** 修改好

<br/>

<br/>

## 若你的Nexus地址不是localhost:8081

<br/>

打开项目Parent的Pom.xml文件，找到distributionManagement标签

```xml
    <distributionManagement>
        <repository>
            <id>nexus-releases</id>
            <name>Nexus Release Repository</name>
            <url>http://修改Nexus地址/repository/maven-releases/</url>
        </repository>
        <snapshotRepository>
            <id>nexus-snapshots</id>
            <name>Nexus Snapshot Repository</name>
            <url>http://修改Nexus地址/repository/maven-snapshots/</url>
        </snapshotRepository>
    </distributionManagement>
```

<br/>

<br/>

## 完成以上步骤即可编译 mvn clean deploy

<br/>

# 测试脚手架前准备

<br/>

## 编译后说明

**Nacos版本固定2.0.4**

该阶段围绕着安装Nacos、添加基础配置进行讲解，目前安装走的Docker Compose进行安装，没有docker的可以拉取Nacos官方代码进行启动，本文档跳过以下安装步骤，导入[Nacos配置文件](https://gitee.com/xmaozi/maozi-cloud-parent-nacos-config)即可

<br/>

## Docker Compose安装Nacos

<br/>

### 1.下载[Nacos Docker](https://gitee.com/xmaozi/maozi-cloud-parent-script/tree/release/nacos/nacos-docker-2.0.4)安装目录

<br/>

### 2.导入[SQL脚本](https://gitee.com/xmaozi/maozi-cloud-parent-script/blob/release/nacos/nacos-config-mysql.sql)

<br/>

### 3.在下载的GitHub项目上做修改

在env目录上修改nacos-standlone-mysql.env文件

```properties
PREFER_HOST_MODE=hostname
MODE=standalone
SPRING_DATASOURCE_PLATFORM=mysql
MYSQL_SERVICE_HOST=Mysql IP
MYSQL_SERVICE_DB_NAME=Nacos数据库名字
MYSQL_SERVICE_PORT=Mysql端口
MYSQL_SERVICE_USER=Mysql用户名 
MYSQL_SERVICE_PASSWORD=Mysql密码
MYSQL_SERVICE_DB_PARAM=characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false&serverTimezone=Asia/Shanghai
```

<br/>

**在example目录上修改standalone-mysql-8.yaml**

**若你的Mysql版本不是8就修改standalone-mysql-5.7.yaml**

<br/>

standalone-mysql-8.yaml

```yaml
version: "2"
services:
  nacos:
    image: nacos/nacos-server:${NACOS_VERSION}
    container_name: nacos-standalone-mysql
    env_file:
      - ../env/nacos-standlone-mysql.env
    volumes:
      - ./standalone-logs/:/home/nacos/logs
      - ./init.d/custom.properties:/home/nacos/init.d/custom.properties
    ports:
      - "8848:8848"
      - "9848:9848"
      - "9555:9555"
    restart: always
```

<br/>

<br/>

standalone-mysql-5.7.yaml

```yaml
version: "2"
services:
  nacos:
    image: nacos/nacos-server:${NACOS_VERSION}
    container_name: nacos-standalone-mysql
    env_file:
      - ../env/nacos-standlone-mysql.env
    volumes:
      - ./standalone-logs/:/home/nacos/logs
      - ./init.d/custom.properties:/home/nacos/init.d/custom.properties
    ports:
      - "8848:8848"
      - "9848:9848"
      - "9555:9555"
    restart: always
```

<br/>

<br/>

### 4.启动Nacos

<br/>

执行

```powershell
docker-compose -f example/standalone-mysql-8.yaml up -d
```

<br/>

<br/>

### 5.修改Nacos配置文件

<br/>

**若Nacos地址是localhost:8848即可忽然步骤**

<br/>

在Nacos配置中搜索

**cloud-dubbo.yml**

**cloud-nacos.yml**

将127.0.0.1:8848修改成Nacos地址

<br/>

<br/>

<br/>

# 测试脚手架

<br/>

**测试项目可以用代码生成项目 或 从作者的仓库里找两个已经创建好的项目进行启动测试**

<br/>

业务项目：[系统服务](https://gitee.com/xmaozi/maozi-cloud-system) 、[授权服务](https://gitee.com/xmaozi/maozi-cloud-sso)

中间件项目：[网关服务](https://gitee.com/xmaozi/maozi-cloud-basics-gateway) 、 [监控服务](https://gitee.com/xmaozi/maozi-cloud-basics-monitor) 、 [流控服务](https://gitee.com/xmaozi/maozi-cloud-basics-sentinel)

拉取代码后即可编译或启动项目 ，**记住不要少了.git文件 不然会编译失败**

<br/>

<br/>

# 创建新业务项目

<br/>

## 生成代码

打开 **maozi-cloud-common\maozi-cloud-common-generate-code\src\main\java\com\maozi\generate\code\GenerateCodeRun.java** 执行

```text
请输入模块名字（users）：users
请输入生成地址（D:\project\maozi-cloud-parent\maozi-cloud-common）：D:\project\maozi-cloud-parent\maozi-cloud-common
请输入是否依赖数据库（yes/no）：yes
请输入数据库地址如 （默认 localhost:3306）：
请输入数据库用户名（默认 root）：
请输入数据库密码（默认 password）：
请输入数据库名（默认 maozi-cloud-users-localhost-db）：maozi-cloud-users-localhost-db
请输入子模块名（输入break结束）：user
请输入表名（输入break结束）：sys_user
请输入表名实体映射过滤前缀：sys
请输入表名（输入break结束）：break
请输入子模块名（输入break结束）：break
正在生成中   。。。。。。
代码生成完成   。。。。。
```

<br/>

<br/>

<br/>

## 创建Nacos配置文件

**文件名为：{项目名字}.yml**

如：maozi-cloud-user.yml

```properties
#端口
application-port: 2000

# 设置数据库参数 以下值都是默认值 , 无变化可注释掉
application-datasource-jdbc-url: localhost:3306
application-datasource-jdbc-username: root
application-datasource-jdbc-password: password
application-datasource-db-name: maozi-cloud-user-localhost-db

#白名单
application-project-whitelist: /user/pc/v1/login
```

<br/>

<br/>

**Nacos地址默认为localhost:8081，若不是则添加环境变量NACOS_CONFIG_SERVER**

或找到 **maozi-cloud-service-sso/src/main/resources/bootstrap.properties** 添加

```
spring.cloud.nacos.config.server-addr={Nacos地址}
```

<br/>

<br/>

## 设置Jvm VM参数

**因为用的是JDK17 所以要设置以下VM参数**

```text
--add-opens java.base/java.math=ALL-UNNAMED  --add-opens java.base/java.lang=ALL-UNNAMED  --add-opens java.base/java.lang.reflect=ALL-UNNAMED
```

<br/>

<br/>

## 启动服务

启动成功 ，访问 **localhost:{端口}/doc.html**

<br/>

<br/>

# 新服务包说明

可看[MarkDown](https://gitee.com/xmaozi/maozi-cloud-system)文档，里面有说明包作用
