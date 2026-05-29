---
name: 项目代码规范
description: 项目代码规范说明，每次新增修改代码时使用
---

# 项目代码规范

1. 所有传输或序列化对象都需要实现序列化以及声明序列化字段 @Serial private static final long serialVersionUID = 1L;
2. 每个字段、每个方法、每个类、每个配置文件都需要注释描述
3. 接口的入参与返回值都需要使用OpenAPI注解说明作用
4. 业务需要使用service实现业务逻辑，再引入到controller，controller只做校验数据，如果校验在service做更方便则就写在service中
5. 接口的入惨要创建param对象，然后再使用CopyUtil进行对象复制到entity，规定只能入惨哪些字段
6. 数据库查出来entity后要通过CopyUtil转换成result对象返回出去，规定只能返回哪些字段数据