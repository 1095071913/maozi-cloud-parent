---
name: 项目代码规范
description: 项目代码规范说明，每次新增修改代码时使用
---

# 项目代码规范
1. 所有传输或序列化对象都需要实现序列化以及声明序列化
```java
@Serial
private static final long serialVersionUID = 1L;
```
2. 每个字段、每个方法、每个类都需要注释描述