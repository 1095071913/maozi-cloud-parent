# maozi-cloud-parent

封装该脚手架的初心：为企业快速开发业务制定一整齐套体系，全体开发人员代码风格一致，才能使团队高效开发维护

该脚手架基于微服务开发，给开发人员快速投入业务开发，封装好了一系列的组件入：日志收集、业务通用方法、框架配置、数据包结果集、监控、常用的第三方接口 等一系列功能，后续会不断升级 框架组件、封装更多通用方法或框架组件、第三方接口、接入Kubesphere Istio

已有16家企业在用的基础脚手架，每次更新发版都会经过Devops压测保证框架基本可用，放心使用

<br/>

<br/>

<br/>

# 架构图

<img src="https://camo.githubusercontent.com/19b6438c99c4cefcd1419d582f3a4d112b44863adc141e7c8b02ea3d0677e6f6/68747470733a2f2f696d672e616c6963646e2e636f6d2f696d6765787472612f69332f4f31434e303141477239744c316d75536868446d757a6e5f2121363030303030303030353031342d302d7470732d323837342d313430342e6a7067" alt="null" style="zoom:50%;" />

<br/>

<br/>

<br/>

# 目录结构

```text

maozi-cloud-common
  maozi-cloud-common-result                     (结果集)
  maozi-cloud-common-utils                      (工具)
  maozi-cloud-common-generate-code              (代码生成工具)
  
  
  
maozi-cloud-config
  maozi-cloud-config-arthas                     (Arthas配置)
  maozi-cloud-config-monitor                    (监控配置)
  maozi-cloud-config-db                         (数据库配置)
  maozi-cloud-config-discovery                  (注册中心配置)
  maozi-cloud-config-dubbo                      (Dubbo配置)
  maozi-cloud-config-feign                      (Feign配置)
  maozi-cloud-config-job                        (定时任务配置)
  maozi-cloud-config-log                        (日志配置)
  maozi-cloud-config-lock                       (分布式锁配置)
  maozi-cloud-config-web                        (web配置)
  maozi-cloud-config-redis                      (Redis配置)
  maozi-cloud-config-seata                      (Seata配置)
  maozi-cloud-config-sentinel                   (Sentinel配置)
  maozi-cloud-config-oauth                      (认证授权配置)
  maozi-cloud-config-stream                     (消息中间件流配置)
  maozi-cloud-config-swagger                    (接口文档配置)
  
  
  
maozi-cloud-entity
  maozi-cloud-base-entity
    maozi-cloud-base-do                         (Domain Object 基层)
    maozi-cloud-base-dto                        (Data Transfer Object 基层)
    maozi-cloud-base-enum                       (枚举 基层)
    maozi-cloud-base-vo                         (View Object 基层)
  maozi-cloud-business-entity             
    maozi-cloud-do                              (Domain Object 聚合依赖)
      maozi-cloud-seata-do                      (服务通用 Seata Domain)  
    maozi-cloud-dto                             (Data Transfer Object 聚合依赖)
      maozi-cloud-oauth-token-dto               (授权服务 Data Transfer Object)
      maozi-cloud-system-user-dto               (系统用户服务 Data Transfer Object)
    maozi-cloud-enum                            (枚举 聚合依赖)
    maozi-cloud-vo                              (View Object 聚合依赖)
  
  
  
maozi-cloud-service
  maozi-cloud-service-base
    maozi-cloud-service-base-api                (服务接口 基层)
    maozi-cloud-service-base-impl               (服务接口实现 基层)
    maozi-cloud-service-base-run                (启动服务 基层)
  maozi-cloud-service-business
    maozi-cloud-service-impl                    (服务聚合依赖 基层)
      maozi-cloud-service-bd                    (百度地图服务接口实现)
      maozi-cloud-service-qny                   (七牛云服务接口实现)
      maozi-cloud-service-ss                    (闪送服务接口实现)
      maozi-cloud-service-company-wechat        (企业微信服务接口实现)
    maozi-cloud-service-rest-api                (服务HTTP接口聚合依赖 基层)
      maozi-cloud-service-rest-api-oauth-token
    maozi-cloud-service-rpc-api                 (服务RPC接口聚合依赖 基层)
      maozi-cloud-service-rpc-api-oauth-token
      maozi-cloud-service-rpc-api-system-config
      maozi-cloud-service-rpc-api-system-user
  maozi-cloud-service-plugin
    maozi-cloud-service-db-impl                 (数据库服务实现 插件基层)
  
  
  
```

<br/>

<br/>

<br/>

# 集成框架版本

|框架|版本|说明|
|:--:|:--:|:--:|
|JDK|17|兼容JDK8，由于Springboot3只支持17所以在Springboot2.6中先用|
|Spring Boot|2.6.11|基于Spring框架的快速开发应用程序框架|
|Spring Cloud|2021.0.4|Netfix推出微服务一站式服务框架|
|Spring Cloud Alibaba|2021.0.4.0|阿里巴巴推出微服务一站式服务框架|
|Dubbo|3.0.8|基于Triple Stream协议 对内 服务与服务之间RPC通信框架|
|Spring Cloud Feign|跟随SC版本|基于HTTP协议 对外 服务与第三方之间RPC通信框架|
|Spring Cloud Alibaba Gateway|跟随SCA版本|黑白名单，鉴权，流量分发 流式处理网关框架|
|Spring Cloud Alibaba Stream|跟随SCA版本|异步消峰消息框架|
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
|Transmittable|2.14.4|ThreadLocal上下文切换传值问题|

<br/>

<br/>

<br/>

# 最佳实践

<br/>

为了能让大家【快速体验/快速开发】，则准备了一套本地开发环境的【最佳实践流程】

<br/>

个人开发模式（ **只支持Windows系统** ）：

1. 采用 Docker-desktop 部署 【整套中间件】 以及 【整套业务服务 （实例为稳定版本的实例）】
2. Idea开发软件 根据业务变化 【按需启动业务变更服务（实例为灰度版本）】
3. Docker-desktop部署的整套业务服务，需要通过【[bat脚本](https://github.com/1095071913/maozi-cloud-script/blob/release/maozi-cloud-build/maozi-cloud-build-all.bat)】自动化按需更新部署服务
4. 该脚本的原理是  扫描Git变更文件，然后进行变更文件的打包部署

<br/>

团队开发模式：

1. 采用一台内网机器安装 WSL2 ，部署 【整套中间件】 以及 【整套业务服务 （实例为稳定版本的实例）】
2. 将WSL2的端口映射到宿主机，然后放行防火墙端口，这时候团队内的开发者就可以连接到 【WSL2】 的 【中间件】 以及 【业务服务】
3. 团队内的开发者使用Idea开发，根据业务变化 【按需启动业务变更服务（实例为灰度版本）】
4. WSL2内部署的整套业务服务，需要通过 GitLab-Runner 自动化按需更新部署服务
5. 在此脚本就不提供给大家了，大家自行根据企业开发团队内规范进行编写自动化脚本吧

<br/>

那么接下来的部署方式将会采用 【个人开发模式】进行引导开发者【快速体验/快速开发】

<br/>

<br/>

<br/>

# 快速入门（个人开发模式）

<br/>

1. Windows安装Docker-desktop
  1. 使用win+R快捷键 , 输入optionalfeatures，然后会打开windows功能窗口
  2. 勾选两项（适用于Linux的windows子系统）与（虚拟机平台）
  3. 重启电脑
  4. 使用CMD指令设置WSL版本
     ```
     wsl --set-default-version 2
     
     wsl update
     ```
  5. 游览器下载[Docker-desktop安装包](https://docs.docker.com/desktop/release-notes)
  6. 安装完后打开 Docker-desktop 面板点击登录旁边的setting设置，点击Docker Engine 输入
     ```
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
2. Docker-desktop运行所有所需 【中间件】
  1. Git拉取对应Parent[脚本](https://github.com/1095071913/maozi-cloud-script/tree/release)仓库，
  2. 进入maozi-cloud-docker/maozi-cloud-basics-docker目录，启动mysql，执行
     ```
     docker-compose up -d maozi-cloud-mysql
     ```
  3. 创建nacos-test-db数据库，将Nacos数据库脚本 maozi-cloud-docker/maozi-cloud-basics-docker/maozi-cloud-nacos 导入
  4. 回到maozi-cloud-docker/maozi-cloud-basics-docker目录，启动剩余中间件，执行
     ```
     docker-compose up -d maozi-cloud-redis maozi-cloud-nacos
     ```

<br/>

3. Docker-desktop运行所有所需 【业务服务】
  1. 拉取所有 中间件服务代码（[Gateway](https://github.com/1095071913/maozi-cloud-basics-gateway)、[Monitor](https://github.com/1095071913/maozi-cloud-basics-monitor)）以及 业务服务代码（[System](https://github.com/1095071913/maozi-cloud-system)、[Oauth](https://github.com/1095071913/maozi-cloud-oauth)）
  2. 将以上服务纳入同一个Maven进行管理，结构为：
     ```
     maozi-cloud-basics
       maozi-cloud-basics-gateway
       maozi-cloud-basics-monitor
       pom.xml
       
     maozi-cloud-services
       maozi-cloud-system
       maozi-cloud-oauth
       pom.xml
     
     ```

     <br/>

     ！！！有的人可能会问 为什么要纳入同一个Maven管理，这里做一个回答：

     当 system 与 oauth 有依赖相互引用时，无论是同步构建 还是并行构建都会出问题，只能交由Maven自行管理分析解析依赖关系

     <br/>
  3. 将 maozi-cloud-parent 、maozi-cloud-basics 、maozi-cloud-services 三大模块的本地路径填写到对应的文件
     ```
     # maozi-cloud-parent
     maozi-cloud-build/maozi-cloud-parent-build/maozi-cloud-parent-directory
     
     # maozi-cloud-basics
     maozi-cloud-build/maozi-cloud-basics-build/maozi-cloud-basics-directory
     
     # maozi-cloud-serivces
     maozi-cloud-build/maozi-cloud-services-build/maozi-cloud-services-directory
     ```
  4. 进入maozi-cloud-build目录，启动所有业务服务，执行
     ```
     .\maozi-cloud-build-all.bat
     ```

     <br/>

     ！！！注意：在当前Git下，分支与SHA没有变化的情况下是不会重启任何服务的，要想重启服务，需要删除脚本执行后生成出来的 **env文件

<br/>

4. 设置 本地开发工具 按需启动的服务配置，并启动服务
  1. 设置本地Hosts文件
     ```
     # maozi-cloud
     127.0.0.1 maozi-cloud-nacos
     
     127.0.0.1 maozi-cloud-redis
     
     127.0.0.1 maozi-cloud-mysql
     ```
  2. 设置 JVM 启动服务参数
     ```
     -Xms512m -Xmx512m -Xmn256m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m --add-opens java.base/java.math=ALL-UNNAMED  --add-opens java.base/java.lang=ALL-UNNAMED  --add-opens java.base/java.lang.reflect=ALL-UNNAMED -Dproject.version=maozi -Dapplication-dev-port=0 -Dapplication-dev-dubbo-port=-1
     ```

<br/>

5. 启动完成后 可以通过 Apifox 进行全流程用例测试
