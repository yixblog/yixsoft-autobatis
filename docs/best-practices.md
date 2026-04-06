# 最佳实践

本文档总结了使用 YixSoft-Autobatis 的最佳实践，帮助您编写高效、可维护的代码。

## 主键生成策略选择

### 场景推荐

| 场景 | 推荐生成器 | 原因 |
|------|-----------|------|
| 通用业务系统 | `UUIDPkProvider` | 全局唯一、无序、安全性高 |
| 高并发插入 | `SnowFlakeLongKeyProvider` | 有序、数值型、性能最佳 |
| 分库分表 | `SnowFlakeStringKeyProvider` | 有序、字符串型、便于路由 |
| 传统数据库 | `Jdbc3KeyGenerator` | 自增、数值型、数据库原生支持 |

### 配置示例

```java
// 高并发场景
@AutoMapper(
    tablename = "orders",
    pkName = "id",
    keyGenerator = SnowFlakeLongKeyProvider.class
)
public interface OrderMapper {
    @AutoSql(SqlType.INSERT)
    void insert(Order order);
}

// 分库分表场景
@AutoMapper(
    tablename = "users",
    pkName = "id",
    keyGenerator = SnowFlakeStringKeyProvider.class,
    keyPrefix = "user_"
)
public interface UserMapper {
    @AutoSql(SqlType.INSERT)
    void insert(User user);  // 主键格式：user_12345678901234
}
```

## null值处理策略

### 场景推荐

| 场景 | 推荐规则 | 原因 |
|------|----------|------|
| 更新操作 | `PARAM_TYPE_DETECT`（默认） | JavaBean忽略null字段，避免误更新 |
| 查询操作 | `ALWAYS` | 灵活查询，忽略null条件 |
| 严格查询 | `NEVER` | null值生成IS NULL条件 |

### 配置示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 默认规则：JavaBean参数忽略null字段
    @AutoSql(SqlType.UPDATE)
    void update(User user);  // user.name=null → name字段不会被更新
    
    // ALWAYS规则：所有参数类型都忽略null值
    @AutoSql(value = SqlType.SELECT, ignoreNullRule = IgnoreNullRule.ALWAYS)
    List<User> selectByCondition(Map<String, Object> condition);
    
    // NEVER规则：null值生成IS NULL条件
    @AutoSql(value = SqlType.SELECT, ignoreNullRule = IgnoreNullRule.NEVER)
    List<User> selectStrict(Map<String, Object> condition);
}
```

## 参数类型选择

### 场景推荐

| 场景 | 推荐参数类型 | 原因 |
|------|-------------|------|
| 单条件查询 | `@Param`单参数 | 简洁明了 |
| 多条件固定查询 | 多`@Param`参数 | 参数名明确、类型安全 |
| 动态条件查询 | `Map`参数 | 灵活、可动态添加条件 |
| 复杂查询对象 | JavaBean参数 | 结构化、类型安全、可复用 |

### 配置示例

```java
// 单条件查询
@AutoSql(SqlType.SELECT)
User selectById(@Param("id") String id);

// 多条件固定查询
@AutoSql(SqlType.SELECT)
List<User> selectByAgeRange(
    @Param("minAge") Integer minAge,
    @Param("maxAge") Integer maxAge
);

// 动态条件查询
@AutoSql(SqlType.SELECT)
List<User> selectByCondition(Map<String, Object> condition);

// 使用示例
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);
condition.put("isActive", true);
if (keyword != null) {
    condition.put("name", keyword);  // 动态添加条件
}
List<User> users = userMapper.selectByCondition(condition);
```

## 性能优化建议

### 1. 减少列查询

使用 `@AdvanceSelect` 排除不必要的列：

```java
// 不推荐：查询所有列（包括大字段）
@AutoSql(SqlType.SELECT)
List<User> selectAll();

// 推荐：排除大字段或敏感字段
@AdvanceSelect(excludeColumns = {"avatar", "password"})
@AutoSql(SqlType.SELECT)
List<User> selectAll();
```

### 2. 使用静态更新

只更新必要字段：

```java
// 不推荐：传入完整对象更新所有字段
@AutoSql(SqlType.UPDATE)
void update(User user);

// 推荐：只更新必要字段
@StaticUpdate("status = #{status}")
void updateStatus(@Param("id") String id, @Param("status") UserStatus status);
```

### 3. 合理使用分页

使用分页插件避免一次性查询大量数据：

```java
// 不推荐：查询所有再手动分页
List<User> all = userMapper.selectAll();
List<User> page = all.subList(0, 20);

// 推荐：使用分页插件
@AutoSql(SqlType.SELECT)
PageList<User> selectByPage(Map<String, Object> condition);
```

### 4. 批量操作使用IN条件

避免循环单条操作：

```java
// 不推荐：循环单条操作
for (String id : ids) {
    userMapper.deleteById(id);
}

// 推荐：使用IN条件批量查询后批量操作
List<User> users = userMapper.selectByIds(ids);
// 再处理批量操作（或使用其他批量工具）
```

## 异常处理最佳实践

### 异常类型识别

| 异常类型 | 触发场景 | 处理方式 |
|----------|----------|----------|
| `MissingTableException` | 表不存在或权限不足 | 检查配置和数据库权限 |
| `AutoSqlException` | 方言未注册、主键为null等 | 检查注解配置 |
| `IllegalArgumentException` | 参数类型错误 | 检查参数传递 |

### 异常处理示例

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
        } catch (IllegalArgumentException e) {
            // 参数错误：检查参数传递
            log.error("Invalid parameter", e);
            throw new BusinessException("参数错误");
        }
    }
}
```

## 与手动SQL混用

自动SQL和手动SQL可以混用，发挥各自优势：

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

## 枚举处理最佳实践

### 使用 @EnumValue 标注

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

// 数据库存储：0, 1, 2（而非 GUEST, MEMBER, VIP）
// 查询时自动根据数值匹配枚举
```

### 枚举查询示例

```java
@AutoSql(SqlType.SELECT)
List<User> selectByStatus(@Param("status") UserStatus status);

// 使用
List<User> vipUsers = userMapper.selectByStatus(UserStatus.VIP);
// SQL: WHERE status = 2
```

## 表名和列名映射最佳实践

### 开启驼峰转换

```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true
```

### 使用 @Column 注解

当列名与字段名不一致时：

```java
public class User {
    @Column("user_name")  // 数据库列名为 user_name
    private String name;
    
    @Column("created_at")
    private Date createdAt;
}
```

## 分页优化最佳实践

### 复杂查询使用自定义计数

```java
// 复杂查询（包含JOIN等）
@AutoSql(SqlType.SELECT)
PageList<User> selectComplexQuery(Map<String, Object> condition);

// 简化的计数查询（不包含JOIN）
@AutoSql(SqlType.COUNT)
int countSimpleQuery(Map<String, Object> condition);

// 关联计数查询
@AdvancedPaginator(countMapperRef = "countSimpleQuery")
@AutoSql(SqlType.SELECT)
PageList<User> selectComplexQueryOptimized(Map<String, Object> condition);
```

### 使用 @CountSqlTpl

```java
@CountSqlTpl("SELECT COUNT(1) FROM users WHERE status = #{status}")
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatusPage(@Param("status") UserStatus status);
```

## 代码组织最佳实践

### Mapper接口命名规范

```java
// 推荐：表名 + Mapper
public interface UserMapper { }
public interface OrderMapper { }
public interface ProductMapper { }

// 不推荐：随意命名
public interface UserDao { }
public interface UserRepo { }
```

### Service层封装

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    // 业务逻辑封装
    public User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setStatus(UserStatus.GUEST);
        userMapper.insert(user);
        return user;  // 返回包含生成主键的对象
    }
    
    public void activateUser(String id) {
        @StaticUpdate("status = #{status}, activated_at = NOW()")
        userMapper.updateStatus(id, UserStatus.MEMBER);
    }
}
```

## 测试最佳实践

### 单元测试示例

```java
@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    
    @Test
    public void testInsertAndSelect() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        
        userMapper.insert(user);
        assertNotNull(user.getId());  // 主键自动生成
        
        User found = userMapper.selectById(user.getId());
        assertEquals("Test User", found.getName());
    }
    
    @Test
    public void testSelectByIds() {
        String[] ids = {"u001", "u002", "u003"};
        List<User> users = userMapper.selectByIds(ids);
        assertEquals(3, users.size());
    }
}
```

## 总结

遵循这些最佳实践可以：

1. **提高性能**：减少不必要的列查询和更新
2. **增强可维护性**：清晰的命名和组织结构
3. **避免常见错误**：合理的异常处理和参数选择
4. **灵活应对场景**：自动SQL和手动SQL的有机结合

记住：YixSoft-Autobatis 旨在简化简单CRUD操作，复杂业务逻辑仍需手动SQL或业务层处理。合理使用两者结合，才能发挥最大价值。