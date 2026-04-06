# PostgreSQL方言配置指南

本文档介绍如何在YixSoft-Autobatis项目中配置和使用PostgreSQL方言。

## 概述

PostgreSQL方言模块（`yixsoft-batis-dialect-pg`）为YixSoft-Autobatis提供PostgreSQL数据库支持，实现完整的SQL自动生成功能。

### 支持的功能

- ✅ 自动表结构查询（通过`information_schema`）
- ✅ 关键字转义（使用双引号）
- ✅ ROW_NUMBER窗口函数支持
- ✅ PostgreSQL数据类型映射
- ✅ 分页查询支持（mybatis-paginator集成）
- ✅ Auto-increment主键回填

### 兼容性

- PostgreSQL版本：9.x - 18.x
- PostgreSQL JDBC驱动：42.x
- Java版本：17+
- MyBatis版本：3.5+

## 快速配置

### 1. 添加Maven依赖

```xml
<!-- Spring Boot Starter -->
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- PostgreSQL方言模块 -->
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-pg</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- PostgreSQL JDBC驱动 -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
</dependency>
```

### 2. Spring Boot配置

```yaml
mybatis:
  # 配置PostgreSQL方言
  dialects:
    - com.yixsoft.support.mybatis.dialects.pg.PostgreSqlDialect

  # 主键生成器（可选）
  default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider

  # MyBatis配置
  configuration:
    map-underscore-to-camel-case: true

  # 分页配置（可选）
  paginator:
    enable: true
    dialect: com.github.miemiedev.mybatis.paginator.dialect.PostgreSQLDialect
```

### 3. 数据源配置

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
```

### 4. 开启蛇形命名转驼峰（推荐）

```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true
```

此配置让MyBatis自动将PostgreSQL的蛇形命名（如`user_id`）映射到Java的驼峰命名（如`userId`），无需在每个字段上添加`@Column`注解。

**映射示例**：

| PostgreSQL列名 | Java字段名 |
|----------------|-----------|
| `user_id` | `userId` |
| `created_at` | `createdAt` |
| `is_active` | `isActive` |

## 使用示例

### 基础CRUD操作

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {

    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") Long id);

    @AutoSql(SqlType.INSERT)
    void insert(User user);

    @AutoSql(SqlType.UPDATE)
    void update(User user);

    @AutoSql(SqlType.DELETE)
    void deleteById(@Param("id") Long id);
}
```

### Auto-increment主键回填

PostgreSQL表定义：
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(200)
);
```

Mapper配置：
```java
@AutoMapper(tablename = "users", pkName = "id", keyGenerator = Jdbc3KeyGenerator.class)
public interface UserMapper {

    @AutoSql(SqlType.INSERT)
    void insert(User user);  // 主键自动回填到user.id
}
```

使用示例：
```java
User user = new User();
user.setUsername("test");
user.setEmail("test@example.com");

userMapper.insert(user);
System.out.println("生成的ID: " + user.getId());  // 主键已回填
```

### 分页查询

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {

    @AutoSql(SqlType.SELECT)
    PageList<User> selectByPage(
        Map<String, Object> params,
        PageBounds pageBounds
    );
}
```

使用示例：
```java
Map<String, Object> params = new HashMap<>();
params.put("status", "active");

PageBounds pageBounds = new PageBounds(1, 10);  // 第1页，每页10条
PageList<User> users = userMapper.selectByPage(params, pageBounds);

System.out.println("总数: " + users.getPaginator().getTotalCount());
System.out.println("当前页数据: " + users);
```

## PostgreSQL特有功能

### JSON/JSONB类型处理

PostgreSQL方言将JSON和JSONB类型映射为`JDBCType.VARCHAR`，用户可自定义TypeHandler处理：

```java
@MappedTypes(JsonNode.class)
public class JsonTypeHandler extends BaseTypeHandler<JsonNode> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json == null) return null;
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to parse JSON", e);
        }
    }

    // 其他方法实现...
}
```

### UUID类型处理

PostgreSQL方言将UUID类型映射为`JDBCType.VARCHAR`：

```java
@MappedTypes(UUID.class)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String uuid = rs.getString(columnName);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    // 其他方法实现...
}
```

## 关键字转义

PostgreSQL使用双引号转义关键字：

```java
// 假设表名是PostgreSQL关键字
@AutoMapper(tablename = "order", pkName = "id")
public interface OrderMapper {
    @AutoSql(SqlType.SELECT)
    List<Order> selectAll();
}

// 生成的SQL：
// SELECT * FROM "order"
```

## 常见问题

### 1. 方言未正确加载

**问题**：启动时报错找不到PostgreSQL方言

**解决方案**：
- 确认已添加`yixsoft-batis-dialect-pg`依赖
- 检查`mybatis.dialects`配置是否正确
- 查看`PostgreSqlDialect`类是否被Spring扫描到

### 2. 数据类型映射不正确

**问题**：某些PostgreSQL特有类型无法正确映射

**解决方案**：
- 查看[PostgreSQL数据类型映射文档](postgresql-type-mapping.md)
- 为特殊类型自定义TypeHandler
- 联系开发者添加新的类型映射支持

### 3. 分页查询失败

**问题**：分页查询时报错

**解决方案**：
- 确认已配置`mybatis.paginator.enable: true`
- 检查`mybatis.paginator.dialect`配置是否为`PostgreSQLDialect`
- 确认`mybatis-paginator`依赖已正确引入

## 性能建议

1. **连接池配置**：使用HikariCP等高性能连接池
2. **表结构缓存**：SqlDialectManager会缓存方言实例，无需担心性能问题
3. **索引优化**：为常用查询条件创建适当的索引
4. **分页优化**：大数据量分页时考虑使用游标分页

## 更多资源

- [快速开始指南](quick-start.md)
- [PostgreSQL数据类型映射](postgresql-type-mapping.md)
- [架构设计](../openspec/specs/architecture.md)
- [扩展机制](../openspec/specs/extension-mechanisms.md)