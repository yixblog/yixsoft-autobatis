# YixSoft-Autobatis

MyBatis 自动 SQL 生成插件 - 通过注解驱动，零配置实现单表 CRUD 操作

## 项目简介

YixSoft-Autobatis 是一个基于 MyBatis 的自动 SQL 生成插件，旨在简化单表 CRUD 操作的开发工作。通过注解驱动的方式，开发者只需定义 Mapper 接口方法签名，系统即可自动生成对应的 SQL 语句。

### 核心特性

- **注解驱动**：通过 `@AutoMapper` 和 `@AutoSql` 注解声明表映射和 SQL 类型
- **自动表结构感知**：运行时自动获取数据库表元数据，确保 SQL 与表结构一致性
- **智能参数处理**：自动处理 null 值判断、集合展开为 IN 条件等场景
- **多数据库支持**：方言系统自动适配 MySQL、Oracle、PostgreSQL 等数据库特性
- **主键自动生成**：支持 UUID、SnowFlake、数据库自增等多种策略

### 适用场景

- 管理后台系统：大量单表 CRUD 操作
- 数据导入导出：批量插入、更新、查询
- API 服务开发：快速构建 RESTful API 数据访问层
- 原型开发：减少前期 SQL 编写工作

### 不适用场景

- 复杂多表关联查询（跨表 JOIN、子查询等）
- 业务逻辑复杂的更新（需要复杂条件判断或计算）
- 性能要求极高的查询（需要定制优化 SQL）

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- 根据使用的数据库选择方言 -->
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-mysql</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- 或使用 PostgreSQL 方言 -->
<!--
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-pg</artifactId>
    <version>2.1.0</version>
</dependency>
-->

<!-- 或使用 Oracle 方言 -->
<!--
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-oracle</artifactId>
    <version>2.1.0</version>
</dependency>
-->
```

### Spring Boot 配置

```yaml
# MySQL 配置
mybatis:
  default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
  configuration:
    map-underscore-to-camel-case: true

# PostgreSQL 配置
# mybatis:
#   default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider
#   dialects:
#     - com.yixsoft.support.mybatis.dialects.pg.PostgreSqlDialect
#   configuration:
#     map-underscore-to-camel-case: true

# Oracle 配置
# mybatis:
#   default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider
#   dialects:
#     - com.yixsoft.support.mybatis.dialects.oracle.OracleDialect
#   configuration:
#     map-underscore-to-camel-case: true
```

### Mapper 定义示例

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
    
    @AutoSql(SqlType.SELECT)
    List<User> selectByIds(@Param("id") String[] ids);  // 自动生成 IN 条件
}
```

## 核心注解

### @AutoMapper

标注在 Mapper 接口上，定义表映射信息。

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `tablename` | String | 是 | 数据库表名 |
| `pkName` | String[] | 是 | 主键列名数组 |
| `keyGenerator` | Class | 否 | 主键生成器类型 |
| `keyPrefix` | String | 否 | 主键前缀 |

### @AutoSql

标注在 Mapper 方法上，声明 SQL 类型。

| SqlType | 说明 |
|---------|------|
| `INSERT` | 插入数据，支持主键自动生成 |
| `UPDATE` | 根据主键更新数据 |
| `SELECT` | 查询数据列表 |
| `DELETE` | 删除数据 |
| `COUNT` | 查询记录数量 |

### 扩展注解

- `@Column`：字段与列名映射
- `@EnumValue`：枚举值映射
- `@StaticUpdate`：静态更新片段
- `@AdvanceSelect`：高级查询配置（排除列、额外条件等）

## 主键生成策略

| 生成器 | 类型 | 适用场景 |
|--------|------|----------|
| `UUIDPkProvider` | String UUID | 通用性强、全局唯一 |
| `SnowFlakeLongKeyProvider` | Long | 有序 ID、性能最佳 |
| `SnowFlakeStringKeyProvider` | String | 分库分表、便于路由 |
| `Jdbc3KeyGenerator` | 自增 | 数据库原生支持 |

## 技术栈

- Java 17+
- MyBatis 3.5+ / MyBatis-Spring 3.0+
- Spring Boot 3.2+ / Spring 6.1+
- Maven 3.9+

## 文档导航

详细文档请参阅 [docs/index.md](docs/index.md)：

- [快速开始指南](docs/quick-start.md)
- [注解详解](docs/annotations.md)
- [最佳实践](docs/best-practices.md)
- [高级用法](docs/advanced-usage.md)
- [架构设计](openspec/specs/architecture.md)
- [扩展机制](openspec/specs/extension-mechanisms.md)

## 版本信息

- 当前版本：2.1.0
- 开源协议：MIT License
- 源码仓库：https://gitee.com/yixsoft/yixsoft-autobatis

## 开发团队

- 主要开发者：Astro Dai (davepotter@163.com)