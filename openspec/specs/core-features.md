# 核心功能详解

## 注解驱动体系

### @AutoMapper - Mapper级别配置

标注在Mapper接口上，定义表映射信息。

```java
@AutoMapper(
    tablename = "users",                    // 表名
    pkName = {"id"},                        // 主键列名（数组支持多列主键）
    keyGenerator = UUIDPkProvider.class,    // 主键生成器
    keyPrefix = "user_"                     // 主键前缀（可选）
)
public interface UserMapper {
    // Mapper方法定义
}
```

#### 属性详解

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `tablename` | String | 是 | 数据库表名 |
| `pkName` | String[] | 是 | 主键列名数组（单列或多列） |
| `keyGenerator` | Class&lt;KeyGenerator&gt; | 否 | 主键生成器类型（单列主键时可选） |
| `keyPrefix` | String | 否 | 主键前缀（UUID/String类型主键时可用） |

#### 主键生成器选择

| 生成器 | 生成类型 | 适用场景 | 示例 |
|--------|----------|----------|------|
| `UUIDPkProvider` | String UUID | 默认选择，通用性强 | `550e8400-e29b-41d4-a716-446655440000` |
| `SnowFlakeStringKeyProvider` | String | 需要有序ID，性能要求高 | `1234567890123456789` |
| `SnowFlakeLongKeyProvider` | Long | 需要数值型有序ID | `1234567890123456789L` |
| `Jdbc3KeyGenerator` | 自增 | 数据库自增列，数值型 | `1, 2, 3, ...` |

#### 多列主键注意事项

使用多列主键时，系统不会自动生成主键值，需要在插入前手动设置所有主键列的值。

```java
@AutoMapper(tablename = "order_items", pkName = {"order_id", "item_id"})
public interface OrderItemMapper {
    @AutoSql(SqlType.INSERT)
    void insert(OrderItem item);  // 需手动设置order_id和item_id
}
```

### @AutoSql - 方法级别配置

标注在Mapper方法上，声明SQL类型。

```java
@AutoSql(
    value = SqlType.SELECT,                 // SQL类型
    ignoreNullRule = IgnoreNullRule.PARAM_TYPE_DETECT  // null值处理规则
)
User selectById(@Param("id") String id);
```

#### SqlType枚举

| 类型 | 对应Provider | SqlCommandType | 说明 |
|------|--------------|----------------|------|
| `INSERT` | InsertSqlProvider | INSERT | 插入数据，支持主键自动生成 |
| `UPDATE` | UpdateSqlProvider | UPDATE | 根据主键更新数据 |
| `SELECT` | SelectSqlProvider | SELECT | 查询数据列表 |
| `DELETE` | DeleteSqlProvider | DELETE | 删除数据 |
| `COUNT` | CountSqlProvider | SELECT | 查询记录数量 |

#### IgnoreNullRule枚举

| 规则 | 说明 | 适用场景 |
|------|------|----------|
| `PARAM_TYPE_DETECT` | **默认规则**：Map参数不忽略null，JavaBean参数忽略null字段 | 智能判断，通用性最好 |
| `ALWAYS` | 始终忽略null值 | 所有参数类型都忽略null，灵活查询 |
| `NEVER` | 不忽略null值 | 所有参数类型都不忽略null，严格匹配 |

#### null值处理示例

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // PARAM_TYPE_DETECT规则（默认）
    @AutoSql(SqlType.UPDATE)  // JavaBean参数会忽略null字段
    void update(User user);  // user.name=null → name字段不会被更新
    
    // ALWAYS规则
    @AutoSql(value = SqlType.SELECT, ignoreNullRule = IgnoreNullRule.ALWAYS)
    List<User> selectByCondition(Map<String, Object> condition);  // 忽略所有null值
    
    // NEVER规则
    @AutoSql(value = SqlType.SELECT, ignoreNullRule = IgnoreNullRule.NEVER)
    List<User> selectStrict(Map<String, Object> condition);  // null值生成IS NULL条件
}
```

### @Column - 字段与列名映射

标注在JavaBean字段或getter方法上，自定义列名映射。

```java
public class User {
    private String id;
    
    @Column("user_name")  // 映射到user_name列（而非默认的name）
    private String name;
    
    @Column("created_at")
    private Date createdAt;
    
    // getter方法上标注也可以
    @Column("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }
}
```

#### 使用场景

1. **列名与字段名不一致**：数据库列名为`user_name`，Java字段名为`name`
2. **下划线转驼峰未开启**：即使未配置`mapUnderscoreToCamelCase`也能正确映射
3. **特殊列名**：列名包含特殊字符或与Java关键字冲突

#### 限制说明

仅作用于自动SQL生成时的参数映射，不影响MyBatis的查询结果映射（需配合MyBatis的`@Result`注解）。

### @EnumValue - 枚举值映射

标注在枚举字段上，指定存储到数据库的值字段。

```java
public enum UserStatus {
    GUEST(0), MEMBER(1), VIP(2);
    
    @EnumValue  // 存储code值而非枚举名称
    private final int code;
    
    UserStatus(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
```

#### 默认行为对比

| 标注情况 | 存储值 | 查询值 |
|----------|--------|--------|
| 未标注 | 枚举名称（如`"VIP"`） | 枚举名称匹配 |
| 已标注 | 标注字段值（如`2`） | 标注字段值匹配 |

#### 双向支持

`@EnumValue`同时支持：
- **序列化**：插入/更新时使用标注字段的值
- **反序列化**：查询结果映射时根据标注字段值匹配枚举

### @StaticUpdate - 静态更新片段

替代`@AutoSql(SqlType.UPDATE)`，指定静态的SET子句。

```java
@AutoMapper(tablename = "users", pkName = "id")
public interface UserMapper {
    
    // 传统UPDATE需要传入完整对象
    @AutoSql(SqlType.UPDATE)
    void update(User user);
    
    // 静态UPDATE，只更新特定字段
    @StaticUpdate("is_valid = 0")
    void markDisabled(@Param("id") String id);
    
    // 生成的SQL: UPDATE users SET is_valid = 0 WHERE id = ?
}
```

#### 使用场景

1. **状态更新**：批量更新状态字段，不需要传入完整对象
2. **逻辑删除**：设置删除标记而非物理删除
3. **审计字段更新**：更新`updated_at`、`updated_by`等字段

### @AdvanceSelect - 高级查询配置

扩展SELECT查询，提供更多控制选项。

```java
@AdvanceSelect(
    excludeColumns = {"password", "salt"},   // 排除敏感列
    addonWhereClause = "is_active = 1",     // 额外WHERE条件
    rowNumberColumns = {"row_num"},          // row_number列名
    rowNumberOvers = {"ORDER BY created_at DESC"}  // row_number over子句
)
@AutoSql(SqlType.SELECT)
List<User> selectActiveUsers(Map<String, Object> condition);
```

#### 属性详解

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| `excludeColumns` | String[] | 排除的列名数组 | 排除敏感字段、大字段（blob/clob） |
| `addonWhereClause` | String | 额外的WHERE条件片段 | 固定过滤条件，如软删除标记 |
| `rowNumberColumns` | String[] | row_number()结果列名 | 用于分页、排序场景 |
| `rowNumberOvers` | String[] | row_number()的OVER子句 | 排序和分组条件 |

#### 生成的SQL示例

```sql
-- 排除列 + 额外条件
SELECT id, name, email FROM users 
WHERE name = #{name} AND is_active = 1

-- row_number示例
SELECT id, name, row_number() over (ORDER BY created_at DESC) as row_num 
FROM users WHERE status = #{status}
```

## SQL生成机制

### WHERE条件智能构建

系统根据参数值类型自动生成不同的WHERE条件。

#### 条件类型映射

| 参数值类型 | 生成的条件 | SQL示例 |
|------------|-----------|---------|
| 非null单值 | 等值条件 | `column = #{param}` |
| null值 | IS NULL条件 | `column IS NULL` |
| 数组/集合 | IN条件 | `column IN (?, ?, ?)` |
| WhereCondition | 自定义条件 | 自定义片段 |

#### IN条件自动展开

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

#### 数组/集合参数处理细节

1. **null元素跳过**：数组中的null元素不生成条件
2. **自动参数命名**：格式为`autoextend_{原参数名}_{索引}`
3. **支持类型**：数组、Collection、List、Set等

### 参数对象处理

#### 参数类型识别

| 参数类型 | 处理方式 | 示例 |
|----------|----------|------|
| 单值类型 | 映射到主键参数 | `selectById(String id)` → WHERE id = #{id} |
| Map类型 | 直接使用键值对 | `condition.get("name")` → WHERE name = #{name} |
| JavaBean | 转换为Map后处理 | `user.getName()` → WHERE name = #{user.name} |

#### JavaBean参数处理流程

```
1. 获取类描述（ClassFieldsDescription，带缓存）
   ↓
2. 解析所有字段，识别@Column标注
   ↓
3. 根据IgnoreNullRule决定是否忽略null字段
   ↓
4. 转换为Map：字段名 → 字段值
   ↓
5. 使用Map构建WHERE条件
```

#### @Param注解使用

单个参数使用`@Param`指定参数名：

```java
@AutoSql(SqlType.SELECT)
User selectById(@Param("id") String id);  // 参数名明确为"id"

@AutoSql(SqlType.SELECT)
List<User> selectByStatus(@Param("status") String status);
```

多参数使用`@Param`为每个参数命名：

```java
@AutoSql(SqlType.SELECT)
List<User> selectByAgeRange(
    @Param("minAge") Integer minAge,
    @Param("maxAge") Integer maxAge
);
// WHERE age >= #{minAge} AND age <= #{maxAge}
```

## 主键自动生成机制

### 生成时机

在INSERT语句执行前，通过MyBatis的`KeyGenerator`接口回调：

```java
public interface KeyGenerator {
    void processBefore(Executor executor, MappedStatement ms, 
                       Statement stmt, Object parameter);
}
```

### AbstractKeyProvider实现

```java
public abstract class AbstractKeyProvider<K> implements IPrimaryKeyProvider {
    @Override
    public void processBefore(...) {
        if (shouldGenerateKey(parameter, keyColumns)) {
            String paramName = resolveParamName(parameter, keyColumns[0]);
            MetaObject metaParam = configuration.newMetaObject(parameter);
            if (metaParam.getValue(paramName) == null) {
                metaParam.setValue(paramName, nextKey());  // 生成并设置主键
            }
        }
    }
    
    protected abstract K nextKey();  // 子类实现具体生成逻辑
}
```

### 各生成器实现

#### UUIDPkProvider

```java
public class UUIDPkProvider extends AbstractKeyProvider<String> {
    @Override
    protected String nextKey() {
        return prefix + UUID.randomUUID().toString();
    }
}
```

特点：
- 无序字符串ID
- 全局唯一性强
- 支持前缀定制

#### SnowFlakeStringKeyProvider

```java
public class SnowFlakeStringKeyProvider extends AbstractKeyProvider<String> {
    private static final StringSequence sequence = new StringSequence();
    
    @Override
    protected String nextKey() {
        return prefix + sequence.nextId();
    }
}
```

特点：
- 有序字符串ID
- 性能高于UUID
- 适合分库分表场景

#### SnowFlakeLongKeyProvider

```java
public class SnowFlakeLongKeyProvider extends AbstractKeyProvider<Long> {
    @Override
    protected Long nextKey() {
        return sequence.nextId();
    }
}
```

特点：
- 数值型有序ID
- 占用空间最小
- 适合高性能场景

### 主键前缀支持

支持前缀的生成器需实现`SupportedPrefix`接口：

```java
public interface SupportedPrefix {
    void setPrefix(String prefix);
}

// 使用示例
@AutoMapper(tablename = "orders", pkName = "id", 
            keyGenerator = UUIDPkProvider.class, keyPrefix = "order_")
public interface OrderMapper {
    @AutoSql(SqlType.INSERT)
    void insert(Order order);  // 主键生成: order_550e8400-...
}
```

## 方言系统详解

### 方言核心接口

```java
public interface ISqlDialect {
    // 初始化方言（注入MapperFactoryBean以便访问数据源）
    void init(MapperFactoryBean parentFactory);
    
    // 查询表的所有列信息
    List<ColumnInfo> selectTableColumns(String tableName);
    
    // 转义SQL关键字（防止与数据库关键字冲突）
    String escapeKeyword(String name);
    
    // 生成row_number片段（分页、排序场景）
    String rowNumberFragment(RowNumberConfig config);
}
```

### ColumnInfo模型

```java
public class ColumnInfo {
    private String column;      // 列名
    private String dataType;    // 数据类型
    private boolean nullable;   // 是否允许null
    private String keyType;     // 键类型（PRI/UNI/MUL）
    private String defaultValue;// 默认值
    private String extra;       // 额外信息（如auto_increment）
    private String comment;     // 列注释
}
```

### 方言注册与发现

#### 注册机制

```java
// Spring Boot配置中注册
mybatis:
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect

// 内部注册流程
SqlDialectManager.register("mysql", MySqlDialect.class);
SqlDialectManager.register("mariadb", MySqlDialect.class);  // 同一个方言支持多个数据库
```

#### @SupportsDatabase注解

```java
@SupportsDatabase({"mysql", "mariadb"})  // 声明支持的数据库类型
public class MySqlDialect implements ISqlDialect {
    // ...
}
```

#### 运行时方言选择

```java
// 通过数据库连接获取产品名
DatabaseMetaData metaData = connection.getMetaData();
String dbName = metaData.getDatabaseProductName();  // "MySQL"

// 根据产品名查找方言
ISqlDialect dialect = SqlDialectManager.getDialect(factory, dbName);
```

### MySQL方言实现

```java
@SupportsDatabase({"mysql", "mariadb"})
public class MySqlDialect implements ISqlDialect {
    private DescMySqlTableMapper tableMapper;
    
    @Override
    public void init(MapperFactoryBean parentFactory) {
        // 创建内部Mapper用于查询表结构
        Configuration config = parentFactory.getSqlSession().getConfiguration();
        config.addMapper(DescMySqlTableMapper.class);
        tableMapper = config.getMapper(DescMySqlTableMapper.class, ...);
    }
    
    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        // 执行 DESC table_name 或查询元数据表
        return tableMapper.descTable(tableName)
            .stream()
            .map(this::convertToColumnInfo)
            .collect(Collectors.toList());
    }
    
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

### Oracle方言实现

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

## 分页集成

### Paginator配置

```yaml
mybatis:
  paginator:
    enable: true                      # 启用mybatis-paginator
    advanced-count: true              # 启用高级计数功能
    dialect: com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect
    async-total-count: false          # 是否异步计算总数
    pool-max-size: 0                  # 异步计数线程池大小
```

### @AdvancedPaginator注解

指定自定义计数查询，避免复杂查询的COUNT性能问题。

```java
@AdvancedPaginator(countMapperRef = "countActiveUsers")
@AutoSql(SqlType.SELECT)
PageList<User> selectActiveUsers(Map<String, Object> condition);

@AutoSql(SqlType.COUNT)
int countActiveUsers(Map<String, Object> condition);  // 简化的计数查询
```

### @CountRef注解

引用其他Mapper方法作为计数查询。

```java
@CountRef(value = "countByStatus", mapper = UserMapper.class)
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatus(@Param("status") String status);
```

### @CountSqlTpl注解

直接指定计数SQL模板。

```java
@CountSqlTpl("SELECT COUNT(1) FROM users WHERE status = #{status}")
@AutoSql(SqlType.SELECT)
PageList<User> selectByStatus(@Param("status") String status);
```

## 异常处理

### MissingTableException

表不存在或无法访问时抛出。

```java
throw new MissingTableException("Table " + tableName + " not found in database");
```

触发场景：
- 表名错误
- 数据库连接权限不足
- 表已被删除

### AutoSqlException

自动SQL相关错误时抛出。

```java
throw new AutoSqlException("Dialect not found: " + dbName);
throw new AutoSqlException("Primary key value is null for UPDATE operation");
```

常见场景：
- 方言未注册
- UPDATE操作主键为null
- 表结构无法识别

## 性能优化建议

### 表结构缓存优化

默认表结构永久缓存，可考虑以下优化：

1. **定期刷新**：定时任务清除缓存，重新加载最新结构
2. **版本控制**：记录表结构版本，变化时刷新缓存
3. **手动刷新**：提供API手动清除缓存

### 参数对象优化

1. **使用Map参数**：避免JavaBean转换开销
2. **字段数量控制**：JavaBean字段不宜过多（影响转换性能）
3. **缓存类描述**：系统已缓存，无需手动处理

### SQL生成优化

1. **避免复杂查询**：复杂查询仍使用手动SQL
2. **合理使用excludeColumns**：排除不必要的列，减少数据传输
3. **使用静态UPDATE**：避免传入完整对象，只更新必要字段