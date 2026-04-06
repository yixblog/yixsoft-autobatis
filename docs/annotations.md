# 注解详解

本文档详细介绍 YixSoft-Autobatis 提供的所有注解及其使用方法。

## 核心注解

### @AutoMapper

标注在 Mapper 接口上，定义表映射信息。

#### 属性说明

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `tablename` | String | 是 | - | 数据库表名 |
| `pkName` | String[] | 是 | - | 主键列名数组（支持单列或多列主键） |
| `keyGenerator` | Class&lt;KeyGenerator&gt; | 否 | UUIDPkProvider | 主键生成器类型 |
| `keyPrefix` | String | 否 | "" | 主键前缀（适用于 String 类型主键） |

#### 使用示例

```java
// 单列主键
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    // ...
}

// 多列主键
@AutoMapper(tablename = "order_items", pkName = {"order_id", "item_id"})
public interface OrderItemMapper {
    // ...
}

// 自定义主键生成器
@AutoMapper(
    tablename = "orders",
    pkName = "id",
    keyGenerator = SnowFlakeStringKeyProvider.class,
    keyPrefix = "order_"
)
public interface OrderMapper {
    // ...
}
```

#### 主键生成器选择

| 生成器 | 类型 | 特点 | 适用场景 |
|--------|------|------|----------|
| `UUIDPkProvider` | String UUID | 无序、全局唯一 | 通用业务系统 |
| `SnowFlakeStringKeyProvider` | String | 有序、字符串 | 分库分表 |
| `SnowFlakeLongKeyProvider` | Long | 有序、数值 | 高并发插入 |
| `Jdbc3KeyGenerator` | 自增 | 数据库原生 | 传统数据库 |

#### 多列主键注意事项

使用多列主键时：
- 系统不会自动生成主键值
- INSERT 操作前需手动设置所有主键列的值
- UPDATE/DELETE 操作需提供所有主键列的值

### @AutoSql

标注在 Mapper 方法上，声明 SQL 类型。

#### 属性说明

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `value` | SqlType[] | 是 | - | SQL 类型（INSERT/UPDATE/SELECT/DELETE/COUNT） |
| `ignoreNullRule` | IgnoreNullRule | 否 | PARAM_TYPE_DETECT | null 值处理规则 |

#### SqlType 类型

| 类型 | 说明 | 生成的 SQL 示例 |
|------|------|-----------------|
| `INSERT` | 插入数据，支持主键自动生成 | `INSERT INTO table (...) VALUES (...)` |
| `UPDATE` | 根据主键更新数据 | `UPDATE table SET ... WHERE pk = ?` |
| `SELECT` | 查询数据列表 | `SELECT ... FROM table WHERE ...` |
| `DELETE` | 删除数据 | `DELETE FROM table WHERE ...` |
| `COUNT` | 查询记录数量 | `SELECT COUNT(*) FROM table WHERE ...` |

#### IgnoreNullRule 规则

| 规则 | 说明 | 适用场景 |
|------|------|----------|
| `PARAM_TYPE_DETECT` | **默认**：Map 参数不忽略 null，JavaBean 参数忽略 null 字段 | 智能判断，通用性最好 |
| `ALWAYS` | 始终忽略 null 值 | 灵活查询，忽略所有 null 条件 |
| `NEVER` | 不忽略 null 值 | 严格匹配，null 值生成 IS NULL 条件 |

#### 使用示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 基本用法
    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") String id);
    
    // 自定义 null 值处理规则
    @AutoSql(value = SqlType.SELECT, ignoreNullRule = IgnoreNullRule.ALWAYS)
    List<User> selectByCondition(Map<String, Object> condition);
    
    // 插入操作
    @AutoSql(SqlType.INSERT)
    void insert(User user);  // 主键自动生成
    
    // 更新操作（默认忽略 JavaBean 的 null 字段）
    @AutoSql(SqlType.UPDATE)
    void update(User user);
}
```

## 字段映射注解

### @Column

标注在 JavaBean 字段或 getter 方法上，自定义列名映射。

#### 属性说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `value` | String | 是 | 数据库列名 |

#### 使用场景

1. **列名与字段名不一致**
2. **下划线转驼峰未开启时**
3. **列名包含特殊字符或与 Java 关键字冲突**

#### 使用示例

```java
public class User {
    private String id;
    
    @Column("user_name")  // 映射到 user_name 列
    private String name;
    
    @Column("created_at")
    private Date createdAt;
    
    // getter 方法上标注也可以
    @Column("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }
}
```

#### 注意事项

仅作用于自动 SQL 生成时的参数映射，不影响 MyBatis 的查询结果映射（需配合 MyBatis 的 `@Result` 注解）。

### @EnumValue

标注在枚举字段上，指定存储到数据库的值字段。

#### 使用示例

```java
public enum UserStatus {
    GUEST(0), MEMBER(1), VIP(2);
    
    @EnumValue  // 存储 code 值而非枚举名称
    private final int code;
    
    UserStatus(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
```

#### 存储对比

| 标注情况 | 存储值 | 查询值 |
|----------|--------|--------|
| 未标注 | 枚举名称（如 `"VIP"`） | 枚举名称匹配 |
| 已标注 | 标注字段值（如 `2`） | 标注字段值匹配 |

#### 双向支持

`@EnumValue` 同时支持：
- **序列化**：插入/更新时使用标注字段的值
- **反序列化**：查询结果映射时根据标注字段值匹配枚举

## 扩展注解

### @StaticUpdate

替代 `@AutoSql(SqlType.UPDATE)`，指定静态的 SET 子句。

#### 属性说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `value` | String | 是 | 静态 SET 子句片段 |

#### 使用场景

1. **状态更新**：批量更新状态字段
2. **逻辑删除**：设置删除标记
3. **审计字段更新**：更新时间戳等字段

#### 使用示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 状态更新
    @StaticUpdate("status = #{status}")
    void updateStatus(@Param("id") String id, @Param("status") UserStatus status);
    // SQL: UPDATE users SET status = ? WHERE id = ?
    
    // 逻辑删除
    @StaticUpdate("is_deleted = 1, deleted_at = NOW()")
    void markDeleted(@Param("id") String id);
    // SQL: UPDATE users SET is_deleted = 1, deleted_at = NOW() WHERE id = ?
    
    // 批量更新
    @StaticUpdate("is_valid = 0")
    void markDisabled(@Param("id") String[] ids);
    // SQL: UPDATE users SET is_valid = 0 WHERE id IN (?, ?, ?)
}
```

### @AdvanceSelect

扩展 SELECT 查询，提供更多控制选项。

#### 属性说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `excludeColumns` | String[] | 否 | 排除的列名数组 |
| `addonWhereClause` | String | 否 | 额外的 WHERE 条件片段 |
| `rowNumberColumns` | String[] | 否 | row_number() 结果列名 |
| `rowNumberOvers` | String[] | 否 | row_number() 的 OVER 子句 |

#### 使用场景

1. **排除敏感字段**：不查询 password、salt 等字段
2. **排除大字段**：不查询 blob、clob 等字段
3. **固定过滤条件**：添加软删除标记等固定条件
4. **排序查询**：使用 row_number 实现排序

#### 使用示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 排除敏感字段
    @AdvanceSelect(excludeColumns = {"password", "salt"})
    @AutoSql(SqlType.SELECT)
    List<User> selectPublicInfo(Map<String, Object> condition);
    
    // 固定过滤条件
    @AdvanceSelect(addonWhereClause = "is_deleted = 0")
    @AutoSql(SqlType.SELECT)
    List<User> selectActiveUsers(Map<String, Object> condition);
    
    // 排除字段 + 固定条件
    @AdvanceSelect(
        excludeColumns = {"password", "salt"},
        addonWhereClause = "is_deleted = 0"
    )
    @AutoSql(SqlType.SELECT)
    List<User> selectSafeActiveUsers(Map<String, Object> condition);
    
    // row_number 排序
    @AdvanceSelect(
        rowNumberColumns = {"row_num"},
        rowNumberOvers = {"ORDER BY created_at DESC"}
    )
    @AutoSql(SqlType.SELECT)
    List<User> selectWithRowNumber(Map<String, Object> condition);
}
```

#### 生成的 SQL 示例

```sql
-- 排除列 + 固定条件
SELECT id, name, email, status FROM users 
WHERE name = #{name} AND is_deleted = 0

-- row_number 示例
SELECT id, name, row_number() over (ORDER BY created_at DESC) as row_num 
FROM users WHERE status = #{status}
```

## 分页注解

### @AdvancedPaginator

指定自定义计数查询，避免复杂查询的 COUNT 性能问题。

#### 属性说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `countMapperRef` | String | 是 | 计数查询方法名 |

#### 使用示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 复杂查询
    @AutoSql(SqlType.SELECT)
    PageList<User> selectComplexQuery(Map<String, Object> condition);
    
    // 简化的计数查询
    @AutoSql(SqlType.COUNT)
    int countSimpleQuery(Map<String, Object> condition);
    
    // 使用 @AdvancedPaginator 关联计数查询
    @AdvancedPaginator(countMapperRef = "countSimpleQuery")
    @AutoSql(SqlType.SELECT)
    PageList<User> selectComplexQueryWithOptimizedCount(Map<String, Object> condition);
}
```

### @CountRef

引用其他 Mapper 方法作为计数查询。

#### 属性说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `value` | String | 是 | 计数查询方法名 |
| `mapper` | Class | 否 | Mapper 接口类型（默认为当前 Mapper） |

#### 使用示例

```java
@CountRef(value = "countByStatus", mapper = UserMapper.class)
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatusPage(@Param("status") UserStatus status);
```

### @CountSqlTpl

直接指定计数 SQL 模板。

#### 属性说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `value` | String | 是 | 计数 SQL 模板 |

#### 使用示例

```java
@CountSqlTpl("SELECT COUNT(1) FROM users WHERE status = #{status}")
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatusPage(@Param("status") UserStatus status);
```

## 参数注解

### @Param

MyBatis 标准注解，用于命名参数。

#### 使用示例

```java
// 单参数
@AutoSql(SqlType.SELECT)
User selectById(@Param("id") String id);

// 多参数
@AutoSql(SqlType.SELECT)
List<User> selectByAgeRange(
    @Param("minAge") Integer minAge,
    @Param("maxAge") Integer maxAge
);
```

#### 注意事项

- 单参数必须使用 `@Param` 注解
- 多参数建议每个参数都使用 `@Param` 注解
- 参数名应与数据库列名一致（或通过 `@Column` 映射）

## 注解组合使用

### 组合示例

```java
@AutoMapper(
    tablename = "users",
    pkName = "id",
    keyGenerator = SnowFlakeStringKeyProvider.class,
    keyPrefix = "user_"
)
public interface UserMapper {
    
    // 基本查询
    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") String id);
    
    // 排除敏感字段的查询
    @AdvanceSelect(excludeColumns = {"password", "salt"})
    @AutoSql(SqlType.SELECT)
    List<User> selectSafeUsers(Map<String, Object> condition);
    
    // 固定过滤条件的查询
    @AdvanceSelect(addonWhereClause = "is_deleted = 0")
    @AutoSql(SqlType.SELECT)
    List<User> selectActiveUsers(Map<String, Object> condition);
    
    // 优化的分页查询
    @AdvancedPaginator(countMapperRef = "countSimple")
    @AutoSql(SqlType.SELECT)
    PageList<User> selectComplexQuery(Map<String, Object> condition);
    
    @AutoSql(SqlType.COUNT)
    int countSimple(Map<String, Object> condition);
    
    // 静态更新
    @StaticUpdate("status = #{status}, updated_at = NOW()")
    void updateStatus(@Param("id") String id, @Param("status") UserStatus status);
}
```

## 总结

YixSoft-Autobatis 提供了丰富的注解体系：

1. **核心注解**：`@AutoMapper` 定义表映射，`@AutoSql` 定义方法 SQL 类型
2. **字段映射注解**：`@Column`、`@EnumValue` 处理字段与列的关系
3. **扩展注解**：`@StaticUpdate`、`@AdvanceSelect` 提供更多控制选项
4. **分页注解**：`@AdvancedPaginator`、`@CountRef`、`@CountSqlTpl` 优化分页性能

合理组合使用这些注解，可以满足大部分单表 CRUD 操作的需求。