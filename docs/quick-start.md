# 快速开始指南

本指南将帮助您在 5 分钟内快速上手 YixSoft-Autobatis。

## 环境要求

- Java 17+
- Spring Boot 3.2+
- MyBatis 3.5+
- Maven 3.9+

## 第一步：添加依赖

在您的 Spring Boot 项目 `pom.xml` 中添加以下依赖：

```xml
<!-- 核心依赖 -->
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
```

如果您使用 Oracle 数据库：

```xml
<dependency>
    <groupId>com.yixsoft</groupId>
    <artifactId>yixsoft-batis-dialect-oracle</artifactId>
    <version>2.0.5</version>
</dependency>
```

## 第二步：配置数据源和方言

在 `application.yml` 中配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  # 方言配置（必需）
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
  
  # 主键生成器（可选，默认为 UUID）
  default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider
  
  # MyBatis 标准配置
  configuration:
    map-underscore-to-camel-case: true
```

## 第三步：创建实体类

定义一个简单的用户实体：

```java
public class User {
    private String id;          // 主键（自动生成）
    private String name;
    private String email;
    private Integer age;
    private UserStatus status;  // 枚举类型
    private Date createdAt;
    
    // getter/setter 方法...
}

// 枚举定义
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

## 第四步：创建 Mapper 接口

使用注解定义 Mapper：

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 插入（主键自动生成）
    @AutoSql(SqlType.INSERT)
    void insert(User user);
    
    // 根据主键查询
    @AutoSql(SqlType.SELECT)
    User selectById(@Param("id") String id);
    
    // 查询所有
    @AutoSql(SqlType.SELECT)
    List<User> selectAll();
    
    // 根据主键更新
    @AutoSql(SqlType.UPDATE)
    void update(User user);
    
    // 根据主键删除
    @AutoSql(SqlType.DELETE)
    void deleteById(@Param("id") String id);
    
    // 条件查询
    @AutoSql(SqlType.SELECT)
    List<User> selectByStatus(@Param("status") UserStatus status);
    
    // IN 条件查询
    @AutoSql(SqlType.SELECT)
    List<User> selectByIds(@Param("id") String[] ids);
    
    // 计数查询
    @AutoSql(SqlType.COUNT)
    int countByStatus(@Param("status") UserStatus status);
}
```

## 第五步：在 Service 中使用

创建 Service 类调用 Mapper：

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User createUser(User user) {
        userMapper.insert(user);
        return user;  // 返回包含生成的主键
    }
    
    public User getUser(String id) {
        return userMapper.selectById(id);
    }
    
    public void updateUser(User user) {
        userMapper.update(user);  // 只更新非 null 字段
    }
    
    public List<User> getUsersByStatus(UserStatus status) {
        return userMapper.selectByStatus(status);
    }
    
    public List<User> getUsersByIds(String[] ids) {
        return userMapper.selectByIds(ids);  // 自动生成 IN 条件
    }
}
```

## 第六步：验证效果

启动应用程序，系统会：

1. 自动连接数据库获取表结构
2. 为 `@AutoSql` 方法自动生成 SQL
3. 缓存表结构信息（后续执行无需重复查询）

生成的 SQL 示例：

```sql
-- INSERT
INSERT INTO users (id, name, email, age, status, created_at) 
VALUES (?, ?, ?, ?, ?, ?)

-- SELECT
SELECT id, name, email, age, status, created_at 
FROM users WHERE id = ?

-- SELECT with IN
SELECT id, name, email, age, status, created_at 
FROM users WHERE id IN (?, ?, ?)

-- UPDATE
UPDATE users SET name = ?, email = ?, age = ? 
WHERE id = ?

-- DELETE
DELETE FROM users WHERE id = ?
```

## 常见场景示例

### 场景1：动态条件查询

使用 Map 参数实现动态查询：

```java
@AutoSql(SqlType.SELECT)
List<User> selectByCondition(Map<String, Object> condition);

// 使用
Map<String, Object> condition = new HashMap<>();
condition.put("status", UserStatus.VIP);
condition.put("age", 25);
List<User> users = userMapper.selectByCondition(condition);
// SQL: WHERE status = ? AND age = ?
```

### 场景2：多参数查询

使用多个 `@Param` 参数：

```java
@AutoSql(SqlType.SELECT)
List<User> selectByAgeRange(
    @Param("minAge") Integer minAge,
    @Param("maxAge") Integer maxAge
);

// 使用
List<User> users = userMapper.selectByAgeRange(20, 30);
// SQL: WHERE age >= ? AND age <= ?
```

### 场景3：逻辑删除

使用 `@StaticUpdate` 实现逻辑删除：

```java
@StaticUpdate("is_deleted = 1, deleted_at = NOW()")
void markDeleted(@Param("id") String id);

// 使用
userMapper.markDeleted("u001");
// SQL: UPDATE users SET is_deleted = 1, deleted_at = NOW() WHERE id = ?
```

## 下一步

- 阅读 [注解详解](annotations.md) 了解所有注解的详细用法
- 查看 [最佳实践](best-practices.md) 学习推荐的使用模式
- 探索 [高级用法](advanced-usage.md) 了解更多功能

## 问题排查

### 方言未注册错误

**错误信息**：`AutoSqlException: Dialect not found`

**解决方案**：在配置中注册方言：

```yaml
mybatis:
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
```

### 表不存在错误

**错误信息**：`MissingTableException: Table xxx not found`

**解决方案**：检查表名配置和数据库连接权限

### 主键生成失败

**错误信息**：主键为 null

**解决方案**：检查 `keyGenerator` 配置，确保使用单列主键

## 总结

通过以上步骤，您已经可以：

1. 零配置实现基础 CRUD 操作
2. 使用注解声明 SQL 类型
3. 支持动态条件和 IN 条件查询
4. 自动生成主键值

YixSoft-Autobatis 通过注解驱动的方式，大幅减少了简单 SQL 的编写工作量，让您更专注于业务逻辑的开发。