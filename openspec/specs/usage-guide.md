# 使用指南

## 快速开始

### 1. Maven依赖配置

在Spring Boot项目的`pom.xml`中添加依赖：

```xml
<!-- 核心starter -->
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-spring-boot-starter</artifactId>
    <version>2.0.5</version>
</dependency>

<!-- 数据库方言（根据使用的数据库选择） -->
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-mysql</artifactId>
    <version>2.0.5</version>
</dependency>

<!-- 或 Oracle方言 -->
<!-- <dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-oracle</artifactId>
    <version>2.0.5</version>
</dependency> -->
```

### 2. Spring Boot配置

在`application.yml`或`application.properties`中配置：

```yaml
# 数据源配置（必需）
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

# MyBatis配置
mybatis:
  # 默认主键生成器（可选，默认为UUIDPkProvider）
  default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider
  
  # 严格表检查（可选）
  enable-strict-table-check: false
  
  # 数据库方言配置（必需）
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
  
  # MyBatis标准配置
  configuration:
    map-underscore-to-camel-case: true
    default-fetch-size: 100
    default-statement-time-out: 30
  
  # 分页插件配置（可选）
  paginator:
    enable: true
    advanced-count: true
    dialect: com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect
    async-total-count: false
    pool-max-size: 0
  
  # MyBatis XML配置文件位置（可选）
  config-location: classpath:mybatis.xml
```

### 3. 创建实体类

```java
public class User {
    private String id;          // UUID主键
    private String name;
    private String email;
    private Integer age;
    private UserStatus status;  // 枚举类型
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    
    // getter/setter方法...
}
```

枚举类型示例：

```java
public enum UserStatus {
    GUEST(0), MEMBER(1), VIP(2);
    
    @EnumValue  // 存储数值而非名称
    private final int code;
    
    UserStatus(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
```

### 4. 创建Mapper接口

```java
@AutoMapper(
    tablename = "users",
    pkName = "id",
    keyGenerator = UUIDPkProvider.class
)
public interface UserMapper {
    
    // 基础CRUD操作
    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") String id);
    
    @AutoSql(SqlType.SELECT)
    List<User> selectAll();
    
    @AutoSql(SqlType.INSERT)
    void insert(User user);  // 主键自动生成
    
    @AutoSql(SqlType.UPDATE)
    void update(User user);
    
    @AutoSql(SqlType.DELETE)
    void deleteById(@Param("id") String id);
    
    // 条件查询
    @AutoSql(SqlType.SELECT)
    List<User> selectByStatus(@Param("status") UserStatus status);
    
    @AutoSql(SqlType.SELECT)
    List<User> selectByAgeRange(@Param("minAge") Integer minAge, 
                                @Param("maxAge") Integer maxAge);
    
    // 计数查询
    @AutoSql(SqlType.COUNT)
    int countByStatus(@Param("status") UserStatus status);
    
    // IN条件查询
    @AutoSql(SqlType.SELECT)
    List<User> selectByIds(@Param("id") String[] ids);
}
```

### 5. 在Service中使用

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User createUser(User user) {
        // 主键自动生成，无需手动设置
        userMapper.insert(user);
        return user;  // 返回包含生成的主键
    }
    
    public User getUser(String id) {
        return userMapper.selectById(id);
    }
    
    public void updateUser(User user) {
        // 只更新非null字段（默认ignoreNullRule）
        userMapper.update(user);
    }
    
    public List<User> getActiveUsers() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("isActive", true);
        return userMapper.selectByCondition(condition);
    }
    
    public List<User> getUsersByStatus(UserStatus status) {
        return userMapper.selectByStatus(status);
    }
    
    public List<User> getUsersByIds(String[] ids) {
        return userMapper.selectByIds(ids);  // 自动生成IN条件
    }
}
```

## 常见使用场景

### 场景1：单表CRUD操作

最基础的使用场景，实现完整的CRUD功能。

```java
@AutoMapper(tablename = "products", pkName = "id")
public interface ProductMapper {
    
    // 插入（主键自动生成）
    @AutoSql(SqlType.INSERT)
    void insert(Product product);
    
    // 根据主键查询
    @AutoSql(SqlType.SELECT)
    Product selectById(@Param("id") String id);
    
    // 查询所有
    @AutoSql(SqlType.SELECT)
    List<Product> selectAll();
    
    // 根据主键更新（只更新非null字段）
    @AutoSql(SqlType.UPDATE)
    void update(Product product);
    
    // 根据主键删除
    @AutoSql(SqlType.DELETE)
    void deleteById(@Param("id") String id);
}
```

### 场景2：条件查询

根据多个条件组合查询。

#### 方式1：Map参数

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

#### 方式2：多参数注解

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

#### 方式3：JavaBean参数

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

注意：JavaBean参数中，多个字段映射到同一列时，需要自定义WhereCondition处理（参考高级用法）。

### 场景3：IN条件查询

#### 数组参数

```java
@AutoSql(SqlType.SELECT)
List<User> selectByIds(@Param("id") String[] ids);

// 使用
String[] ids = {"u001", "u002", "u003"};
List<User> users = userMapper.selectByIds(ids);
// SQL: SELECT ... WHERE id IN (?, ?, ?)
```

#### List参数

```java
@AutoSql(SqlType.SELECT)
List<User> selectByNames(@Param("name") List<String> names);

// 使用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<User> users = userMapper.selectByNames(names);
// SQL: SELECT ... WHERE name IN (?, ?, ?)
```

#### 集合参数null值处理

```java
String[] ids = {"u001", null, "u003"};  // 包含null元素
List<User> users = userMapper.selectByIds(ids);
// SQL: WHERE id IN (?, ?)  // null元素被跳过
```

### 场景4：分页查询

集成mybatis-paginator实现分页。

```java
@AutoSql(SqlType.SELECT)
PageList<User> selectByPage(Map<String, Object> condition);

// 使用示例
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);

// 分页查询（默认第1页，每页20条）
PageList<User> page1 = userMapper.selectByPage(condition);

// 指定页码和每页数量
PageList<User> page2 = userMapper.selectByPage(condition, 2, 20);

// 获取总数
int total = page1.getTotalCount();

// 获取数据列表
List<User> users = page1.getList();
```

### 场景5：高级分页优化

使用自定义计数查询避免复杂查询的COUNT性能问题。

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
PageList<User> selectComplexQuery(Map<String, Object> condition);
```

或使用`@CountSqlTpl`直接指定计数SQL：

```java
@CountSqlTpl("SELECT COUNT(1) FROM users WHERE status = #{status}")
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatusPage(@Param("status") UserStatus status);
```

### 场景6：排除敏感字段查询

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

### 场景7：固定过滤条件

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

### 场景8：静态更新操作

```java
// 状态批量更新
@StaticUpdate("status = #{status}")
void updateStatus(@Param("id") String[] ids, @Param("status") UserStatus status);

// 使用
String[] ids = {"u001", "u002", "u003"};
userMapper.updateStatus(ids, UserStatus.VIP);
// SQL: UPDATE users SET status = ? WHERE id IN (?, ?, ?)

// 逻辑删除
@StaticUpdate("is_deleted = 1, deleted_at = NOW()")
void markDeleted(@Param("id") String id);

// 使用
userMapper.markDeleted("u001");
// SQL: UPDATE users SET is_deleted = 1, deleted_at = NOW() WHERE id = ?
```

### 场景9：多列主键操作

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

### 场景10：row_number排序查询

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

## 最佳实践

### 1. 主键生成策略选择

| 场景 | 推荐生成器 | 原因 |
|------|-----------|------|
| 通用业务系统 | `UUIDPkProvider` | 全局唯一、无序、安全性高 |
| 高并发插入 | `SnowFlakeLongKeyProvider` | 有序、数值型、性能最佳 |
| 分库分表 | `SnowFlakeStringKeyProvider` | 有序、字符串型、便于路由 |
| 传统数据库 | `Jdbc3KeyGenerator` | 自增、数值型、数据库原生支持 |

### 2. null值处理策略

| 场景 | 推荐规则 | 原因 |
|------|----------|------|
| 更新操作 | `PARAM_TYPE_DETECT`（默认） | JavaBean忽略null字段，避免误更新 |
| 查询操作 | `ALWAYS` | 灵活查询，忽略null条件 |
| 严格查询 | `NEVER` | null值生成IS NULL条件 |

### 3. 参数类型选择

| 场景 | 推荐参数类型 | 原因 |
|------|-------------|------|
| 单条件查询 | `@Param`单参数 | 简洁明了 |
| 多条件固定查询 | 多`@Param`参数 | 参数名明确、类型安全 |
| 动态条件查询 | `Map`参数 | 灵活、可动态添加条件 |
| 复杂查询对象 | JavaBean参数 | 结构化、类型安全、可复用 |

### 4. 性能优化建议

#### 4.1 减少列查询

```java
// 不推荐：查询所有列（包括大字段）
@AutoSql(SqlType.SELECT)
List<User> selectAll();

// 推荐：排除大字段或敏感字段
@AdvanceSelect(excludeColumns = {"avatar", "password"})
@AutoSql(SqlType.SELECT)
List<User> selectAll();
```

#### 4.2 使用静态更新

```java
// 不推荐：传入完整对象更新所有字段
@AutoSql(SqlType.UPDATE)
void update(User user);

// 推荐：只更新必要字段
@StaticUpdate("status = #{status}")
void updateStatus(@Param("id") String id, @Param("status") UserStatus status);
```

#### 4.3 合理使用分页

```java
// 不推荐：查询所有再手动分页
List<User> all = userMapper.selectAll();
List<User> page = all.subList(0, 20);

// 推荐：使用分页插件
@AutoSql(SqlType.SELECT)
PageList<User> selectByPage(Map<String, Object> condition, int pageNo, int pageSize);
```

#### 4.4 批量操作使用IN条件

```java
// 不推荐：循环单条操作
for (String id : ids) {
    userMapper.deleteById(id);
}

// 推荐：使用IN条件批量查询后批量操作
List<User> users = userMapper.selectByIds(ids);
// 再处理批量操作（或使用其他批量工具）
```

### 5. 异常处理

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User getUser(String id) {
        try {
            return userMapper.selectById(id);
        } catch (MissingTableException e) {
            // 表不存在：检查配置或数据库权限
            log.error("Table users not found", e);
            throw new BusinessException("系统配置错误");
        } catch (AutoSqlException e) {
            // 自动SQL错误：检查注解配置
            log.error("AutoSql configuration error", e);
            throw new BusinessException("系统配置错误");
        }
    }
}
```

### 6. 与手动SQL混用

自动SQL和手动SQL可以混用：

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 自动SQL：简单CRUD
    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") String id);
    
    @AutoSql(SqlType.INSERT)
    void insert(User user);
    
    // 手动SQL：复杂查询
    @Select("SELECT u.*, d.name as dept_name FROM users u LEFT JOIN departments d ON u.dept_id = d.id WHERE u.status = #{status}")
    List<UserWithDept> selectWithDepartment(@Param("status") UserStatus status);
    
    // 手动SQL：复杂更新
    @Update("UPDATE users SET login_count = login_count + 1, last_login = NOW() WHERE id = #{id}")
    void incrementLoginCount(@Param("id") String id);
}
```

## 常见问题与解决方案

### Q1：表结构与JavaBean不匹配

**问题**：数据库列名`user_name`，Java字段名`name`，查询结果映射失败。

**解决方案**：使用`@Column`注解或配置`mapUnderscoreToCamelCase`。

```java
// 方式1：使用@Column注解
@Column("user_name")
private String name;

// 方式2：配置全局映射
mybatis:
  configuration:
    map-underscore-to-camel-case: true
```

### Q2：枚举类型存储为名称而非数值

**问题**：枚举存储为字符串`"VIP"`而非数值`2`。

**解决方案**：使用`@EnumValue`标注数值字段。

```java
public enum UserStatus {
    VIP(2);
    
    @EnumValue  // 标注数值字段
    private final int code;
    
    UserStatus(int code) {
        this.code = code;
    }
}
```

### Q3：UPDATE操作误更新所有字段为null

**问题**：传入JavaBean更新时，null字段被更新为null值。

**解决方案**：使用默认的`PARAM_TYPE_DETECT`规则，JavaBean参数会自动忽略null字段。

```java
@AutoSql(SqlType.UPDATE)  // 默认使用PARAM_TYPE_DETECT
void update(User user);  // null字段不会被更新
```

或使用静态更新：

```java
@StaticUpdate("status = #{status}")
void updateStatus(@Param("id") String id, @Param("status") UserStatus status);
```

### Q4：RowBounds与JavaBean参数冲突

**问题**：使用RowBounds分页时，JavaBean参数抛出调用异常。

**解决方案**：使用Map参数替代JavaBean参数，或使用PageList分页。

```java
// 不推荐
@AutoSql(SqlType.SELECT)
List<User> selectByCondition(UserQuery query, RowBounds rowBounds);

// 推荐：使用Map参数
@AutoSql(SqlType.SELECT)
List<User> selectByCondition(Map<String, Object> condition, RowBounds rowBounds);

// 推荐：使用PageList
@AutoSql(SqlType.SELECT)
PageList<User> selectByCondition(Map<String, Object> condition);
```

### Q5：方言未注册错误

**问题**：启动时抛出`AutoSqlException: Dialect not found`。

**解决方案**：在配置中注册对应的方言类。

```yaml
mybatis:
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
```

或自定义方言并注册：

```java
@SupportsDatabase("customdb")
public class CustomDialect implements ISqlDialect {
    // 实现方言接口
}
```

### Q6：表结构缓存过期

**问题**：数据库表结构变化后，自动SQL仍使用旧结构。

**解决方案**：重启应用清除缓存，或提供API手动清除缓存（需自行实现）。

```java
// 潜在的缓存清除API（需自行实现）
@RestController
public class CacheController {
    @PostMapping("/cache/table-structure/clear")
    public void clearTableCache() {
        TableStructureUtils.clearCache();  // 需自行实现
    }
}
```

### Q7：多字段映射同一列的查询

**问题**：JavaBean中`minAge`和`maxAge`都映射到`age`列，无法生成范围查询。

**解决方案**：使用多`@Param`参数或自定义WhereCondition（需自行实现扩展）。

```java
// 推荐：使用多参数
@AutoSql(SqlType.SELECT)
List<User> selectByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

// 高级：自定义WhereCondition（需自行扩展）
// 参考 AbstractSqlProvider.buildWhereClause() 实现自定义条件
```

### Q8：复杂WHERE条件无法生成

**问题**：需要生成`LIKE`、`BETWEEN`、`OR`等复杂条件。

**解决方案**：使用手动SQL或addonWhereClause。

```java
// 方式1：使用addonWhereClause
@AdvanceSelect(addonWhereClause = "name LIKE CONCAT('%', #{keyword}, '%')")
@AutoSql(SqlType.SELECT)
List<User> searchByKeyword(@Param("keyword") String keyword);

// 方式2：使用手动SQL
@Select("SELECT * FROM users WHERE name LIKE #{keyword} OR email LIKE #{keyword}")
List<User> searchByKeyword(@Param("keyword") String keyword);
```

## 总结

YixSoft-Autobatis通过注解驱动简化了单表CRUD操作的开发，核心使用要点：

1. **核心注解**：`@AutoMapper`定义表映射，`@AutoSql`定义方法SQL类型
2. **参数灵活**：支持单值、Map、JavaBean、数组/集合等多种参数类型
3. **智能条件**：自动识别参数值类型生成对应WHERE条件
4. **主键自动**：支持UUID、SnowFlake、自增等多种主键生成策略
5. **方言适配**：支持MySQL、Oracle等多数据库，自动选择方言
6. **扩展机制**：支持静态更新、高级查询、分页优化等高级功能

合理使用自动SQL和手动SQL的结合，既能减少重复工作，又能保证复杂查询的灵活性。