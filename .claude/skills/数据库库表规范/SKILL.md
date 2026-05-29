---
name: 数据库库表规范
description: 数据库库表规范说明，新增修改数据库库表时使用
---

# 数据库库表规范
1. 字符集使用: utf8mb4
2. 排序规则使用: utf8mb4_general_ci
3. 所有字段必须给默认值
4. flyway的sql文件不能修改或删除，只能新增sql文件
5. 数据库脚本的版本号命名跟随pom.xml的revision版本号