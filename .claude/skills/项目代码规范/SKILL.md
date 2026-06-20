---
name: 项目代码规范
description: 项目代码规范说明，每次新增修改代码时使用
---

# 项目代码规范

1. 所有用于网络传输或序列化的对象必须实现 Serializable 接口，并显式声明序列化版本号：@Serial private static final long serialVersionUID = 1L;
2. 口的入参与返回值必须使用 OpenAPI 注解（@Schema、@Parameter 等）标注，明确各字段的作用与含义
3. 接口的入参与返回值必须使用 OpenAPI 注解（@Schema、@Parameter 等）标注，明确各字段的作用与含义
4. 业务逻辑统一在 Service 层实现，Controller 层仅负责参数校验与请求转发，若参数校验在 Service 层实现更便捷，可下沉至 Service 层处理
5. 接口入参需定义独立的 Param 对象接收，通过 CopyUtil 复制到 Entity 后再进行业务处理，严格限定入参允许传入的字段范围
6. 数据库查询出的 Entity 对象需通过 CopyUtil 转换为 Result 对象后再返回，严格限定对外暴露的返回字段