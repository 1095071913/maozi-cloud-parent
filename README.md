# Istio + SpringCloud Alibaba Dubbo + K8S 

## 概述

该项目由**忧伤-蓝河**团队共同完成 ,  团队内都是多家公司的首席架构师

团队架构历史 :  单体  ->  SOA Server  ->  微服务  ->   Server Mesh   ->  Server Less           ,             LaaS  -> PaaS  ->  SaaS ->  BaaS  -> FasS

团队开发模式 :  遵循  ->     极限编程 、敏捷开发 、DevOps 、领域模型 、代码重构 、无状态 、轻应用          等架构思想


## 后端技术

技术 | 说明 
----|----
JDK13 | ZGC垃圾回收 , var类型
Istio | 新一代微服务 2.0 服务网格 
Spring Cloud Alibaba | 阿里巴巴基于 Spring Cloud 编程模型的微服务生态 
Spring Cloud Alibaba Dubbo  | 与 Spring Cloud Alibaba 生态相结合的高性能 Java RPC 框架
Spring Cloud Nacos | 服务治理与分布式配置中心结合
Spring Cloud Ribbon | 本地负载均衡
Spring Cloud Feign | 基于HTTP服务远程调用
Spring Cloud GateWay | 基于webflux网关、限流、熔断、过滤、降级
Spring Cloud Sentinel | 阿里巴巴双十一限流、熔断
Spring Cloud Skywalking | 探针形链路跟踪
Spring Boot Admin | 监控服务健康状态/参数
Spring Boot | 新一代 JavaEE 开发标准
Spring Security oAuth2 | 基于Oauth2协议实现SSO单点登录、安全认证、授权框架
Kryo | Dubbo高性能序列化
LCN | TCC分布式事务
Elastic-Job | 分布式任务调度
Sharing-JDBC | 数据库分表分库
Spring Social | 第三方登录接入框架  
OKHttp3 | 轻量级网络框架
WebSocket | 长连接网络框架
Swagger-bootstrap-ui | 基于Swagger二次开发API文档生成工具
MyBatisPlus | 基于 MyBatis 二次开发的轻量级框架，用于简化 MyBatis 操作 
PageHelper | MyBatis 分页插件
DDD | 团队共同设计的领域模型,让开发再次减少百分80的代码量,更加专注于业务

## 中间件

技术 | 说明 
----|----
LVS | 虚拟VIP
Keepalived | 基于虚拟VIP实现高可用、单点故障问题
Haproxy | 四层网络模型实现高可用、负载均衡
Nginx | 实现反向代理、负载均衡等
Undertow | 高吞吐量Web容器
Redis | 高性能 K V  Nosql数据库
Zookeeper | 分布式协调
Elasticsearch | 倒排索引搜索引擎
ELK | 实现Elasticsearch数据同步
EFK | 实现分布式日志系统
RabbitMq | 高并发消息队列
Kafka | 高吞吐量消息队列
MyCat | 数据库分表分库
NFS | 网络文件传输
SLB | 阿里实现高可用代理者
TiDB | 分布式数据库


## DevOps

技术 | 说明 
----|----
GitLab | 分布式代码托管
Nexus | Maven私服依赖管理
ZenTao | 项目管理
Harbor | Docker私服镜像存储
GitLab-Runner | CI
Ansible | 无状态管理多台云 Kubernetes 服务器
Jenkins | CD
Docker | 容器化引擎
Docker Compose | 容器编排工具
Kubernetes | 容器编排系统
Kubernetes Helm | 用于 Kubernetes 应用的包管理工具
Prometheus Grafana | 自动监控报警系统



## 该项目由**忧伤-蓝河**团队共同 有问题则联系QQ/微信：1095071913 

2019.8.16
