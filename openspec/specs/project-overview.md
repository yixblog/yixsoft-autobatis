# YixSoft-Autobatis 项目概述

## 项目定位

YixSoft-Autobatis 是一个基于 MyBatis 的自动 SQL 生成插件，旨在简化单表 CRUD 操作的开发工作，减少手动编写重复性 SQL 语句的工作量。

## 核心价值

### 解决的问题

1. **重复性SQL编写**：传统MyBatis开发需要为每个Mapper编写XML或注解SQL，大量简单CRUD操作占用开发时间
2. **表结构维护成本**：当表结构变化时需要同步修改SQL语句，容易遗漏或出错
3. **方言适配困难**：不同数据库的SQL语法差异需要分别处理

### 提供的价值

1. **零SQL编写**：通过注解驱动，自动生成标准CRUD SQL，开发者只需定义方法签名
2. **自动表结构感知**：运行时自动获取数据库表元数据，确保SQL与表结构一致性
3. **多数据库支持**：方言系统自动适配不同数据库特性，统一使用体验
4. **智能参数处理**：自动处理参数类型转换、null值判断、集合展开为IN条件等

## 技术栈

- **核心框架**：MyBatis 3.5+、MyBatis-Spring 3.0+
- **集成框架**：Spring Boot 3.2+、Spring 6.1+
- **语言版本**：Java 17+
- **支持数据库**：MySQL/MariaDB、Oracle（可扩展）
- **构建工具**：Maven 3.9+

## 模块组成

### 核心模块结构

```
yixsoft-autobatis/
├── yixsoft-batis/                    # 核心自动SQL生成引擎
├── yixsoft-batis-dialects-core/      # 方言系统核心接口
├── yixsoft-batis-dialect-mysql/      # MySQL/MariaDB方言实现
├── yixsoft-batis-dialect-oracle/     # Oracle方言实现
├── yixsoft-batis-spring-boot-starter/ # Spring Boot自动配置
└── yixsoft-cnadapter/                # 兼容性适配器
```

### 模块职责

| 模块 | 核心职责 |
|------|---------|
| **yixsoft-batis-dialects-core** | 定义方言接口`ISqlDialect`、列信息模型`ColumnInfo`、方言管理器`SqlDialectManager` |
| **yixsoft-batis-dialect-mysql** | 实现MySQL方言，通过`DESC`命令或元数据查询获取表结构 |
| **yixsoft-batis-dialect-oracle** | 实现Oracle方言，适配Oracle特有的SQL语法 |
| **yixsoft-batis** | 核心引擎：注解解析、SQL生成Provider、参数处理、表结构缓存 |
| **yixsoft-batis-spring-boot-starter** | Spring Boot自动配置、属性绑定、SqlSessionFactory定制 |

## 适用场景

### 最佳适用场景

1. **管理后台系统**：大量单表CRUD操作，表结构相对简单稳定
2. **数据导入导出**：批量插入、更新、查询场景
3. **API服务开发**：快速构建RESTful API的数据访问层
4. **原型开发**：快速搭建项目原型，减少前期SQL编写工作

### 不适用场景

1. **复杂多表关联查询**：跨表JOIN、子查询等复杂SQL仍需手动编写
2. **业务逻辑复杂的更新**：需要复杂条件判断或计算的场景
3. **性能要求极高的查询**：需要定制优化SQL的场景

## 快速开始

### Maven依赖配置

```xml
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-spring-boot-starter</artifactId>
    <version>2.0.5</version>
</dependency>

<!-- 添加对应数据库方言 -->
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-mysql</artifactId>
    <version>2.0.5</version>
</dependency>
```

### Spring Boot配置

```yaml
mybatis:
  default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
  configuration:
    mapUnderscoreToCamelCase: true
```

### Mapper定义示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") String id);
    
    @AutoSql(SqlType.INSERT)
    void insert(User user);  // 主键自动生成
    
    @AutoSql(SqlType.UPDATE)
    void update(User user);
    
    @AutoSql(SqlType.DELETE)
    void deleteById(@Param("id") String id);
}
```

## 开发团队

- **主要开发者**：Astro Dai (davepotter@163.com)
- **开源协议**：MIT License
- **源码仓库**：https://gitee.com/yixsoft/yixsoft-autobatis
- **发布渠道**：Maven Central、GitHub Packages、私有Nexus

## 版本历史

- **当前版本**：2.0.5
- **Java版本要求**：Java 17+（从2.0.x版本开始）
- **Spring Boot版本**：3.2.3+
- **MyBatis版本**：3.5.0+

## 文档体系

1. **项目概述**（本文档）：整体介绍与快速开始
2. **架构设计**：技术架构与核心机制详解
3. **核心功能**：注解体系、SQL生成、参数处理等功能详解
4. **使用指南**：完整使用示例与最佳实践
5. **扩展机制**：方言扩展、自定义Provider等定制化指南