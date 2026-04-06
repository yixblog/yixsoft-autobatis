# PostgreSQL数据类型映射

本文档详细说明YixSoft-Autobatis PostgreSQL方言如何将PostgreSQL数据类型映射到JDBC类型。

## 概述

PostgreSQL支持丰富的数据类型，包括标准SQL类型和PostgreSQL特有类型。PostgreSQL方言模块负责将这些类型映射到标准的`java.sql.JDBCType`，以确保MyBatis能够正确处理数据。

## 标准数据类型映射

### 整数类型

| PostgreSQL类型 | JDBC类型 | Java类型 | 说明 |
|----------------|----------|----------|------|
| `SMALLINT` / `INT2` | `SMALLINT` | `Short` | 2字节整数 |
| `INTEGER` / `INT` / `INT4` | `INTEGER` | `Integer` | 4字节整数 |
| `BIGINT` / `INT8` | `BIGINT` | `Long` | 8字节整数 |

### 字符串类型

| PostgreSQL类型 | JDBC类型 | Java类型 | 说明 |
|----------------|----------|----------|------|
| `CHARACTER` / `CHAR` / `BPCHAR` | `CHAR` | `String` | 定长字符串 |
| `CHARACTER VARYING` / `VARCHAR` | `VARCHAR` | `String` | 变长字符串 |
| `TEXT` | `LONGVARCHAR` | `String` | 长文本 |

### 布尔类型

| PostgreSQL类型 | JDBC类型 | Java类型 | 说明 |
|----------------|----------|----------|------|
| `BOOLEAN` / `BOOL` | `BOOLEAN` | `Boolean` | 布尔值 |

### 时间类型

| PostgreSQL类型 | JDBC类型 | Java类型 | 说明 |
|----------------|----------|----------|------|
| `DATE` | `DATE` | `java.time.LocalDate` | 日期 |
| `TIME` / `TIME WITHOUT TIME ZONE` | `TIME` | `java.time.LocalTime` | 时间 |
| `TIME WITH TIME ZONE` / `TIMETZ` | `TIME_WITH_TIMEZONE` | `java.time.OffsetTime` | 带时区的时间 |
| `TIMESTAMP` / `TIMESTAMP WITHOUT TIME ZONE` | `TIMESTAMP` | `java.time.LocalDateTime` | 时间戳 |
| `TIMESTAMP WITH TIME ZONE` / `TIMESTAMPTZ` | `TIMESTAMP_WITH_TIMEZONE` | `java.time.OffsetDateTime` | 带时区的时间戳 |

### 数值类型

| PostgreSQL类型 | JDBC类型 | Java类型 | 说明 |
|----------------|----------|----------|------|
| `REAL` / `FLOAT4` | `FLOAT` | `Float` | 单精度浮点数 |
| `DOUBLE PRECISION` / `FLOAT8` | `DOUBLE` | `Double` | 双精度浮点数 |
| `NUMERIC` / `DECIMAL` | `NUMERIC` | `java.math.BigDecimal` | 精确数值 |

### 二进制类型

| PostgreSQL类型 | JDBC类型 | Java类型 | 说明 |
|----------------|----------|----------|------|
| `BYTEA` | `BINARY` | `byte[]` | 字节数组 |

## PostgreSQL特有类型映射

PostgreSQL特有类型默认映射为`VARCHAR`，用户可通过自定义TypeHandler实现更精确的处理。

### JSON类型

| PostgreSQL类型 | 默认JDBC类型 | 推荐Java类型 | 说明 |
|----------------|-------------|-------------|------|
| `JSON` | `VARCHAR` | `String` / `JsonNode` | JSON文本 |
| `JSONB` | `VARCHAR` | `String` / `JsonNode` | 二进制JSON |

**自定义TypeHandler示例**：

```java
@MappedTypes(JsonNode.class)
public class JsonTypeHandler extends BaseTypeHandler<JsonNode> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType)
            throws SQLException {
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

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        if (json == null) return null;
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to parse JSON", e);
        }
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (json == null) return null;
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new SQLException("Failed to parse JSON", e);
        }
    }
}
```

### UUID类型

| PostgreSQL类型 | 默认JDBC类型 | 推荐Java类型 | 说明 |
|----------------|-------------|-------------|------|
| `UUID` | `VARCHAR` | `java.util.UUID` | UUID标识符 |

**自定义TypeHandler示例**：

```java
@MappedTypes(UUID.class)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String uuid = rs.getString(columnName);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String uuid = rs.getString(columnIndex);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String uuid = cs.getString(columnIndex);
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
```

### XML类型

| PostgreSQL类型 | 默认JDBC类型 | 推荐Java类型 | 说明 |
|----------------|-------------|-------------|------|
| `XML` | `VARCHAR` | `String` | XML数据 |

### Money类型

| PostgreSQL类型 | 默认JDBC类型 | 推荐Java类型 | 说明 |
|----------------|-------------|-------------|------|
| `MONEY` | `VARCHAR` | `String` / `BigDecimal` | 货币金额 |

### 数组类型

PostgreSQL支持数组类型，方言将其映射为`VARCHAR`：

| PostgreSQL类型 | 默认JDBC类型 | 推荐Java类型 | 说明 |
|----------------|-------------|-------------|------|
| `INTEGER[]` | `VARCHAR` | `Integer[]` / `List<Integer>` | 整数数组 |
| `TEXT[]` | `VARCHAR` | `String[]` / `List<String>` | 文本数组 |
| 其他类型数组 | `VARCHAR` | 对应Java数组 | 其他类型数组 |

**注意**：数组类型需要自定义TypeHandler处理，建议使用PostgreSQL JDBC驱动提供的数组支持。

### SERIAL类型（自增主键）

| PostgreSQL类型 | 底层类型 | 说明 | MyBatis配置 |
|----------------|---------|------|-------------|
| `SERIAL` | `INTEGER` | 4字节自增整数 | `keyGenerator = Jdbc3KeyGenerator.class` |
| `BIGSERIAL` | `BIGINT` | 8字节自增整数 | `keyGenerator = Jdbc3KeyGenerator.class` |
| `SMALLSERIAL` | `SMALLINT` | 2字节自增整数 | `keyGenerator = Jdbc3KeyGenerator.class` |

**示例**：

```java
// PostgreSQL表定义
// CREATE TABLE users (id BIGSERIAL PRIMARY KEY, username VARCHAR(100));

@AutoMapper(tablename = "users", pkName = "id", keyGenerator = Jdbc3KeyGenerator.class)
public interface UserMapper {
    @AutoSql(SqlType.INSERT)
    void insert(User user);  // 主键自动回填
}
```

## 类型映射源码

PostgreSQL方言的类型映射逻辑位于`PostgreSqlDialect.mapPostgreSqlTypeToJdbcType()`方法：

```java
private JDBCType mapPostgreSqlTypeToJdbcType(String postgreSqlType) {
    if (postgreSqlType == null) {
        return JDBCType.VARCHAR;
    }

    String type = postgreSqlType.toLowerCase();

    // 整数类型
    if ("integer".equals(type) || "int".equals(type) || "int4".equals(type)) {
        return JDBCType.INTEGER;
    }
    // ... 其他类型映射

    // 默认映射为VARCHAR
    return JDBCType.VARCHAR;
}
```

## 自定义类型映射

如果需要添加新的类型映射，有以下几种方式：

### 方式1：扩展方言类

创建自定义方言类继承`PostgreSqlDialect`：

```java
@SupportsDatabase("postgresql")
public class CustomPostgreSqlDialect extends PostgreSqlDialect {

    @Override
    protected JDBCType mapPostgreSqlTypeToJdbcType(String postgreSqlType) {
        // 添加自定义类型映射
        if ("my_custom_type".equalsIgnoreCase(postgreSqlType)) {
            return JDBCType.VARCHAR;  // 或其他合适的JDBC类型
        }

        // 其他类型使用父类映射
        return super.mapPostgreSqlTypeToJdbcType(postgreSqlType);
    }
}
```

### 方式2：使用TypeHandler

为特定类型注册全局TypeHandler：

```java
@Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry()
                .register(JsonNode.class, JsonTypeHandler.class);
            configuration.getTypeHandlerRegistry()
                .register(UUID.class, UUIDTypeHandler.class);
        };
    }
}
```

## 常见问题

### 1. 数据类型无法识别

**问题**：某些PostgreSQL类型未正确映射

**解决方案**：
- 检查PostgreSQL类型名称是否标准
- 为特殊类型创建TypeHandler
- 提交Issue请求添加新类型支持

### 2. NULL值处理

**问题**：NULL值的类型推断错误

**解决方案**：
- 在ColumnInfo中检查`allowNull`属性
- 使用Java包装类型而非基本类型

### 3. 数组类型处理复杂

**问题**：PostgreSQL数组类型映射为VARCHAR后难以使用

**解决方案**：
- 使用PostgreSQL JDBC驱动的原生数组支持
- 创建自定义TypeHandler处理数组转换

## 参考资料

- [PostgreSQL数据类型文档](https://www.postgresql.org/docs/current/datatype.html)
- [JDBC类型映射规范](https://docs.oracle.com/javase/8/docs/api/java/sql/JDBCType.html)
- [MyBatis TypeHandler指南](https://mybatis.org/mybatis-3/configuration.html#typeHandlers)