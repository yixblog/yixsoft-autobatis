# 高级用法

本文档介绍 YixSoft-Autobatis 的高级功能和使用技巧。

## 动态条件查询

### WHERE条件智能构建

系统根据参数值类型自动生成不同的WHERE条件。

| 参数值类型 | 生成的条件 | SQL示例 |
|------------|-----------|---------|
| 非null单值 | 等值条件 | `column = #{param}` |
| null值 | IS NULL条件 | `column IS NULL` |
| 数组/集合 | IN条件 | `column IN (?, ?, ?)` |

### IN条件自动展开

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    @AutoSql(SqlType.SELECT)
    List<User> selectByIds(@Param("id") String[] ids);
    
    // 传入 ids = ["u001", "u002", "u003"]
    // 生成SQL: SELECT ... WHERE id IN (?, ?, ?)
    // 参数展开为: id = #{autoextend_id_0}, #{autoextend_id_1}, #{autoextend_id_2}
}
```

### 集合参数null值处理

```java
String[] ids = {"u001", null, "u003"};  // 包含null元素
List<User> users = userMapper.selectByIds(ids);
// SQL: WHERE id IN (?, ?)  // null元素被跳过
```

## 多列主键操作

### 定义多列主键

```java
@AutoMapper(tablename = "order_items", pkName = {"order_id", "item_id"})
public interface OrderItemMapper {
    
    // 多列主键查询
    @AutoSql(SqlType.SELECT)
    OrderItem selectByPrimaryKey(
        @Param("order_id") String orderId,
        @Param("item_id") String itemId
    );
    
    // 多列主键更新（需要提供所有主键列值）
    @AutoSql(SqlType.UPDATE)
    void update(OrderItem item);
    
    // 多列主键删除
    @AutoSql(SqlType.DELETE)
    void deleteByPrimaryKey(
        @Param("order_id") String orderId,
        @Param("item_id") String itemId
    );
    
    // 注意：INSERT需要手动设置所有主键列值
    @AutoSql(SqlType.INSERT)
    void insert(OrderItem item);  // 需手动设置order_id和item_id
}
```

### 多列主键注意事项

使用多列主键时，系统不会自动生成主键值，需要在插入前手动设置所有主键列的值。

## row_number排序查询

### 使用row_number

```java
@AdvanceSelect(
    rowNumberColumns = {"row_num"},
    rowNumberOvers = {"ORDER BY created_at DESC"}
)
@AutoSql(SqlType.SELECT)
List<User> selectWithRowNumber(Map<String, Object> condition);

// 使用
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);
List<User> users = userMapper.selectWithRowNumber(condition);
// SQL: SELECT ..., row_number() over (ORDER BY created_at DESC) as row_num FROM users WHERE status = ?
```

### 分组row_number

```java
@AdvanceSelect(
    rowNumberColumns = {"row_num"},
    rowNumberOvers = {"PARTITION BY dept_id ORDER BY salary DESC"}
)
@AutoSql(SqlType.SELECT)
List<Employee> selectByDepartmentRank(Map<String, Object> condition);
```

## 固定过滤条件

### 软删除场景

```java
// 软删除场景：自动添加is_deleted=0条件
@AdvanceSelect(addonWhereClause = "is_deleted = 0")
@AutoSql(SqlType.SELECT)
List<User> selectActiveUsers(Map<String, Object> condition);

// 使用
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);
List<User> users = userMapper.selectActiveUsers(condition);
// SQL: SELECT ... WHERE status = ? AND is_deleted = 0
```

### 多租户场景

```java
@AdvanceSelect(addonWhereClause = "tenant_id = #{tenantId}")
@AutoSql(SqlType.SELECT)
List<Order> selectByTenant(
    @Param("tenantId") String tenantId,
    Map<String, Object> condition
);

// 使用
List<Order> orders = orderMapper.selectByTenant("tenant001", condition);
// SQL: WHERE ... AND tenant_id = ?
```

## 排除敏感字段查询

### 排除密码字段

```java
@AdvanceSelect(excludeColumns = {"password", "salt"})
@AutoSql(SqlType.SELECT)
List<User> selectPublicInfo(Map<String, Object> condition);

// 使用
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);
List<User> users = userMapper.selectPublicInfo(condition);
// SQL: SELECT id, name, email, status, ... FROM users WHERE status = ?
// 注意：password和salt列被排除
```

### 排除大字段

```java
@AdvanceSelect(excludeColumns = {"avatar", "profile_image", "document_content"})
@AutoSql(SqlType.SELECT)
List<User> selectBasicInfo(Map<String, Object> condition);
```

## 静态更新操作

### 状态批量更新

```java
@StaticUpdate("status = #{status}")
void updateStatus(@Param("id") String[] ids, @Param("status") UserStatus status);

// 使用
String[] ids = {"u001", "u002", "u003"};
userMapper.updateStatus(ids, UserStatus.VIP);
// SQL: UPDATE users SET status = ? WHERE id IN (?, ?, ?)
```

### 逻辑删除

```java
@StaticUpdate("is_deleted = 1, deleted_at = NOW()")
void markDeleted(@Param("id") String id);

// 使用
userMapper.markDeleted("u001");
// SQL: UPDATE users SET is_deleted = 1, deleted_at = NOW() WHERE id = ?
```

### 审计字段更新

```java
@StaticUpdate("updated_at = NOW(), updated_by = #{userId}")
void touchRecord(@Param("id") String id, @Param("userId") String userId);
```

## 高级分页优化

### 自定义计数查询

复杂查询的COUNT性能问题可以通过自定义计数查询解决：

```java
// 复杂查询（包含JOIN等）
@AutoSql(SqlType.SELECT)
PageList<User> selectComplexQuery(Map<String, Object> condition);

// 简化的计数查询（不包含JOIN）
@AutoSql(SqlType.COUNT)
int countSimpleQuery(Map<String, Object> condition);

// 使用@AdvancedPaginator关联计数查询
@AdvancedPaginator(countMapperRef = "countSimpleQuery")
@AutoSql(SqlType.SELECT)
PageList<User> selectComplexQueryOptimized(Map<String, Object> condition);
```

### 使用@CountRef

引用其他Mapper方法作为计数查询：

```java
@CountRef(value = "countByStatus", mapper = UserMapper.class)
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatus(@Param("status") String status);
```

### 使用@CountSqlTpl

直接指定计数SQL模板：

```java
@CountSqlTpl("SELECT COUNT(1) FROM users WHERE status = #{status}")
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatusPage(@Param("status") UserStatus status);
```

## 条件查询组合

### Map参数动态条件

```java
@AutoSql(SqlType.SELECT)
List<User> selectByCondition(Map<String, Object> condition);

// 使用示例
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);
condition.put("isActive", true);
condition.put("age", 25);
List<User> users = userMapper.selectByCondition(condition);
// SQL: WHERE status = ? AND is_active = ? AND age = ?

// null值处理（ALWAYS规则）
condition.put("email", null);  // null值会被忽略（不生成条件）
// SQL: WHERE status = ? AND is_active = ? AND age = ? （email条件不生成）

// 数组参数自动展开
condition.put("id", new String[]{"u001", "u002", "u003"});
// SQL: WHERE id IN (?, ?, ?)
```

### 多参数注解

```java
@AutoSql(SqlType.SELECT)
List<User> selectByCondition(
    @Param("status") UserStatus status,
    @Param("isActive") Boolean isActive,
    @Param("minAge") Integer minAge,
    @Param("maxAge") Integer maxAge
);

// 使用示例
List<User> users = userMapper.selectByCondition(
    UserStatus.VIP, 
    true, 
    20, 
    30
);
// SQL: WHERE status = ? AND is_active = ? AND age >= ? AND age <= ?
```

### JavaBean参数

```java
public class UserQuery {
    private UserStatus status;
    private Boolean isActive;
    private Integer minAge;
    private Integer maxAge;
    
    @Column("age")  // 映射到age列
    public Integer getMinAge() { return minAge; }
    
    @Column("age")  // 映射到age列（同一列）
    public Integer getMaxAge() { return maxAge; }
}

@AutoSql(SqlType.SELECT)
List<User> search(UserQuery query);

// 使用示例
UserQuery query = new UserQuery();
query.setStatus(UserStatus.VIP);
query.setIsActive(true);
query.setMinAge(20);
query.setMaxAge(30);
List<User> users = userMapper.search(query);
```

## 复杂WHERE条件处理

### 使用addonWhereClause

```java
// LIKE条件
@AdvanceSelect(addonWhereClause = "name LIKE CONCAT('%', #{keyword}, '%')")
@AutoSql(SqlType.SELECT)
List<User> searchByKeyword(@Param("keyword") String keyword);

// OR条件
@AdvanceSelect(addonWhereClause = "(status = #{status1} OR status = #{status2})")
@AutoSql(SqlType.SELECT)
List<User> selectByMultipleStatus(
    @Param("status1") UserStatus status1,
    @Param("status2") UserStatus status2
);
```

### 混用手动SQL

复杂查询仍需手动SQL：

```java
@Select("SELECT * FROM users WHERE name LIKE #{keyword} OR email LIKE #{keyword}")
List<User> searchByKeyword(@Param("keyword") String keyword);

@Select("SELECT u.*, d.name as dept_name FROM users u LEFT JOIN departments d ON u.dept_id = d.id WHERE u.status = #{status}")
List<UserWithDept> selectWithDepartment(@Param("status") UserStatus status);
```

## 方言适配

### MySQL方言

```java
@SupportsDatabase({"mysql", "mariadb"})
public class MySqlDialect implements ISqlDialect {
    @Override
    public String escapeKeyword(String name) {
        return "`" + name + "`";  // MySQL反引号转义
    }
    
    @Override
    public String rowNumberFragment(RowNumberConfig config) {
        return "row_number() over (%s) as %s"
            .formatted(config.getOver(), config.getAsColumn());
    }
}
```

### Oracle方言

```java
@SupportsDatabase("oracle")
public class OracleDialect implements ISqlDialect {
    @Override
    public String escapeKeyword(String name) {
        return "\"" + name + "\"";  // Oracle双引号转义
    }
    
    @Override
    public String rowNumberFragment(RowNumberConfig config) {
        // Oracle特有的row_number语法
        return "row_number() over (%s) %s"
            .formatted(config.getOver(), config.getAsColumn());
    }
}
```

### 方言对比

| 特性 | MySQL | Oracle |
|------|-------|--------|
| 关键字转义 | 反引号 ` | 双引号 " |
| 表结构查询 | DESC命令或元数据表 | 查询元数据视图 |
| row_number语法 | 标准SQL:2003语法 | Oracle特有语法 |
| 数据类型映射 | 自定义处理 | Oracle类型适配 |

## 自定义扩展

### 扩展方言

```java
@SupportsDatabase("postgresql")
public class PostgreSqlDialect implements ISqlDialect {
    @Override
    public String escapeKeyword(String name) {
        return "\"" + name + "\"";
    }
    
    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        // 实现PostgreSQL元数据查询
    }
}

// 注册方言
SqlDialectManager.register("postgresql", PostgreSqlDialect.class);
```

### 扩展主键生成器

```java
public class CustomTimestampKeyProvider extends AbstractKeyProvider<String> {
    @Override
    protected String nextKey() {
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("%d%04d", timestamp, random);
    }
}

// 使用
@AutoMapper(
    tablename = "orders",
    pkName = "id",
    keyGenerator = CustomTimestampKeyProvider.class
)
public interface OrderMapper {
    @AutoSql(SqlType.INSERT)
    void insert(Order order);
}
```

## 表结构缓存

### 缓存机制

表结构信息会被缓存，避免重复查询数据库元数据：

- 首次访问表结构时查询数据库
- 缓存结果供后续使用
- 应用重启时清除缓存

### 缓存清除

如需手动清除缓存（需自行实现API）：

```java
@RestController
public class CacheController {
    @PostMapping("/cache/table-structure/clear")
    public void clearTableCache() {
        TableStructureUtils.clearCache();  // 需自行实现
    }
}
```

## 性能调优

### SQL生成性能

1. **使用Map参数**：避免JavaBean转换开销
2. **字段数量控制**：JavaBean字段不宜过多（影响转换性能）
3. **缓存类描述**：系统已缓存，无需手动处理

### 查询性能

1. **避免复杂查询**：复杂查询仍使用手动SQL
2. **合理使用excludeColumns**：排除不必要的列，减少数据传输
3. **使用静态UPDATE**：避免传入完整对象，只更新必要字段

## 总结

高级用法关键点：

1. **动态条件构建**：自动识别参数类型生成对应WHERE条件
2. **多列主键支持**：支持复合主键操作（需手动设置主键值）
3. **row_number支持**：用于分页、排序等场景
4. **固定过滤条件**：自动添加软删除、多租户等条件
5. **静态更新优化**：只更新必要字段，提高性能
6. **分页优化**：自定义计数查询避免复杂查询的COUNT性能问题
7. **方言适配**：支持MySQL、Oracle等多数据库
8. **自定义扩展**：支持方言、主键生成器等扩展

合理使用这些高级功能，可以应对更多复杂场景，同时保持代码简洁和高效。