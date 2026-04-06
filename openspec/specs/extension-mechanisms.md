# 扩展机制指南

## 方言扩展

### 创建新方言

当需要支持新的数据库类型时，可以通过实现`ISqlDialect`接口创建方言。

#### 步骤1：创建方言类

```java
package com.example.mybatis.dialects;

@SupportsDatabase("postgresql")  // 声明支持的数据库类型
public class PostgreSqlDialect implements ISqlDialect {
    
    private PostgreSqlTableMapper tableMapper;
    
    @Override
    public void init(MapperFactoryBean parentFactory) {
        // 初始化方言，通常需要创建内部Mapper用于查询表结构
        Configuration configuration = parentFactory.getSqlSession().getConfiguration();
        
        if (!configuration.hasMapper(PostgreSqlTableMapper.class)) {
            configuration.addMapper(PostgreSqlTableMapper.class);
        }
        
        tableMapper = configuration.getMapper(
            PostgreSqlTableMapper.class,
            parentFactory.getSqlSession()
        );
    }
    
    @Override
    public List<ColumnInfo> selectTableColumns(String tableName) {
        // 查询表的所有列信息
        List<PostgreSqlColumnDao> columns = tableMapper.selectColumns(tableName);
        
        // 转换为标准ColumnInfo
        return columns.stream()
            .map(this::convertToColumnInfo)
            .collect(Collectors.toList());
    }
    
    @Override
    public String escapeKeyword(String name) {
        // PostgreSQL使用双引号转义关键字
        return "\"" + name + "\"";
    }
    
    @Override
    public String rowNumberFragment(RowNumberConfig config) {
        // PostgreSQL支持标准row_number语法
        return "row_number() over (%s) as %s"
            .formatted(config.getOver(), config.getAsColumn());
    }
    
    private ColumnInfo convertToColumnInfo(PostgreSqlColumnDao dao) {
        ColumnInfo info = new ColumnInfo();
        info.setColumn(dao.getColumnName());
        info.setDataType(dao.getDataType());
        info.setNullable("YES".equals(dao.getIsNullable()));
        info.setKeyType(dao.getKeyType());
        info.setDefaultValue(dao.getColumnDefault());
        info.setComment(dao.getComment());
        return info;
    }
}
```

#### 步骤2：创建表结构查询Mapper

```java
package com.example.mybatis.dialects;

public interface PostgreSqlTableMapper {
    
    @Select("""
        SELECT 
            column_name,
            data_type,
            is_nullable,
            column_default,
            character_maximum_length,
            numeric_precision,
            numeric_scale,
            ordinal_position
        FROM information_schema.columns
        WHERE table_name = #{tableName}
        ORDER BY ordinal_position
        """)
    List<PostgreSqlColumnDao> selectColumns(@Param("tableName") String tableName);
}
```

#### 步骤3：定义列信息DAO

```java
package com.example.mybatis.dialects;

public class PostgreSqlColumnDao {
    private String columnName;
    private String dataType;
    private String isNullable;
    private String columnDefault;
    private Integer characterMaximumLength;
    private Integer numericPrecision;
    private Integer numericScale;
    private Integer ordinalPosition;
    
    // getter/setter方法...
    
    public String getKeyType() {
        // PostgreSQL的键类型判断逻辑（需要查询其他元数据）
        return "NONE";  // 示例，实际需根据索引信息判断
    }
}
```

#### 步骤4：注册方言

在Spring Boot配置中注册方言：

```yaml
mybatis:
  dialects:
    - com.example.mybatis.dialects.PostgreSqlDialect
```

或通过代码注册：

```java
@Configuration
public class DialectConfig {
    @PostConstruct
    public void registerDialects() {
        SqlDialectManager.register("postgresql", PostgreSqlDialect.class);
    }
}
```

#### 步骤5：创建方言模块（可选）

建议将方言作为独立Maven模块发布：

```xml
<!-- pom.xml -->
<project>
    <groupId>com.example</groupId>
    <artifactId>yixsoft-batis-dialect-postgresql</artifactId>
    <version>1.0.0</version>
    
    <dependencies>
        <dependency>
            <groupId>com.yixsoft</groupId>
            <artifactId>yixsoft-batis-dialects-core</artifactId>
            <version>2.0.5</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.0</version>
        </dependency>
    </dependencies>
</project>
```

### 方言实现要点

#### 关键字转义

不同数据库使用不同的转义符号：

| 数据库 | 转义符号 | 示例 |
|--------|----------|------|
| MySQL | 反引号 ` | \`name\` |
| Oracle | 双引号 " | "name" |
| PostgreSQL | 双引号 " | "name" |
| SQL Server | 方括号 [] | [name] |

#### 表结构查询

不同数据库获取表结构的方式：

| 数据库 | 查询方式 | SQL示例 |
|--------|----------|---------|
| MySQL | DESC命令或元数据表 | `DESC table_name` 或查询 `information_schema.columns` |
| Oracle | 元数据视图 | 查询 `ALL_TAB_COLUMNS` 或 `USER_TAB_COLUMNS` |
| PostgreSQL | 元数据表 | 查询 `information_schema.columns` |
| SQL Server | 元数据视图 | 查询 `sys.columns` |

#### row_number语法

部分数据库的row_number实现差异：

| 数据库 | 语法支持 | 备注 |
|--------|----------|------|
| MySQL 8.0+ | 标准SQL:2003语法 | `row_number() over (...) as ...` |
| MySQL 5.x | 不支持 | 需使用变量模拟 |
| Oracle | Oracle特有语法 | 需调整over和as位置 |
| PostgreSQL | 标准SQL:2003语法 | 与MySQL 8.0+相同 |
| SQL Server | 标准SQL:2003语法 | 与MySQL 8.0+相同 |

## 主键生成器扩展

### 创建自定义主键生成器

#### 步骤1：实现IPrimaryKeyProvider接口

```java
package com.example.mybatis.pk;

import com.yixsoft.support.mybatis.autosql.pk.IPrimaryKeyProvider;
import com.yixsoft.support.mybatis.autosql.pk.AbstractKeyProvider;
import org.apache.ibatis.executor.keygen.KeyGenerator;

public class CustomTimestampKeyProvider extends AbstractKeyProvider<String> 
    implements IPrimaryKeyProvider {
    
    @Override
    protected String nextKey() {
        // 自定义主键生成逻辑
        // 示例：基于时间戳的字符串主键
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("%d%04d", timestamp, random);
    }
}
```

#### 步骤2：支持主键前缀（可选）

```java
package com.example.mybatis.pk;

import com.yixsoft.support.mybatis.autosql.pk.AbstractKeyProvider;
import com.yixsoft.support.mybatis.autosql.pk.IPrimaryKeyProvider;
import com.yixsoft.support.mybatis.autosql.pk.SupportedPrefix;

public class CustomTimestampKeyProvider extends AbstractKeyProvider<String> 
    implements IPrimaryKeyProvider, SupportedPrefix {
    
    private String prefix = "";
    
    @Override
    protected String nextKey() {
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + String.format("%d%04d", timestamp, random);
    }
    
    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
```

#### 步骤3：使用自定义主键生成器

```java
@AutoMapper(
    tablename = "orders",
    pkName = "id",
    keyGenerator = CustomTimestampKeyProvider.class,
    keyPrefix = "order_"
)
public interface OrderMapper {
    
    @AutoSql(SqlType.INSERT)
    void insert(Order order);  // 主键格式：order_12345678901234
}
```

### 主键生成器实现要点

#### processBefore方法

主键生成器在INSERT执行前被调用：

```java
@Override
public void processBefore(Executor executor, MappedStatement ms, 
                         Statement stmt, Object parameter) {
    // 1. 检查是否应该生成主键
    String[] keyColumns = ms.getKeyColumns();
    if (keyColumns.length != 1) return;  // 多列主键不自动生成
    
    // 2. 解析主键参数名
    Configuration configuration = ms.getConfiguration();
    MetaObject metaParam = configuration.newMetaObject(parameter);
    
    // 3. 检查主键值是否为null
    if (metaParam.getValue(keyColumns[0]) == null) {
        // 4. 生成主键并设置
        metaParam.setValue(keyColumns[0], nextKey());
    }
}
```

#### 参数名解析

需要处理JavaBean参数的属性名映射：

```java
protected String resolveParamName(Object parameter, String columnName) {
    if (parameter instanceof Map) {
        return columnName;  // Map参数直接使用列名
    }
    
    // JavaBean参数需要查找@Column标注的字段
    ClassFieldsDescription<?> desc = ClassFieldsDescriptionCache.get(parameter.getClass());
    ColumnFieldInfo fieldInfo = ColumnFieldInfo.findMatchField(desc, columnName);
    
    return fieldInfo != null ? fieldInfo.getFieldName() : columnName;
}
```

#### 线程安全考虑

主键生成器实例可能被多线程并发调用：

```java
// 推荐：使用ThreadLocal或原子类保证线程安全
public class CustomKeyProvider extends AbstractKeyProvider<Long> {
    private final AtomicLong sequence = new AtomicLong(0);
    
    @Override
    protected Long nextKey() {
        return sequence.incrementAndGet();  // 原子操作，线程安全
    }
}

// 或使用ThreadLocalRandom
public class CustomKeyProvider extends AbstractKeyProvider<String> {
    @Override
    protected String nextKey() {
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return System.currentTimeMillis() + String.valueOf(random);
    }
}
```

## SQL Provider扩展

### 创建自定义SQL Provider

#### 场景：支持LIKE查询

假设需要自动生成LIKE查询条件，可以创建自定义Provider。

#### 步骤1：创建LikeCondition

```java
package com.example.mybatis.where;

import com.yixsoft.support.mybatis.autosql.core.where.WhereCondition;

public class LikeCondition implements WhereCondition {
    private final Object value;
    private final boolean prefixMatch;  // 前缀匹配
    private final boolean suffixMatch;  // 后缀匹配
    
    public LikeCondition(Object value) {
        this(value, true, true);  // 默认前后都匹配
    }
    
    public LikeCondition(Object value, boolean prefixMatch, boolean suffixMatch) {
        this.value = value;
        this.prefixMatch = prefixMatch;
        this.suffixMatch = suffixMatch;
    }
    
    @Override
    public String buildWhereCondition(String columnName, String key, 
                                      Map<String, Object> additionalParam) {
        additionalParam.put("__like_" + key, buildPattern());
        return columnName + " LIKE #{__like_" + key + "}";
    }
    
    private String buildPattern() {
        StringBuilder pattern = new StringBuilder();
        if (prefixMatch) pattern.append("%");
        pattern.append(value);
        if (suffixMatch) pattern.append("%");
        return pattern.toString();
    }
}
```

#### 步骤2：创建LikeSqlProvider

```java
package com.example.mybatis.providers;

import com.yixsoft.support.mybatis.autosql.core.providers.SelectSqlProvider;

public class LikeSelectSqlProvider extends SelectSqlProvider {
    
    @Override
    protected void buildWhereClause() {
        Map<String, Object> param = getParam();
        Map<String, ColumnInfo> columns = getTableColumnMap();
        ISqlDialect dialect = getDialect();
        
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            String columnName = findFieldReferColumn(fieldName);
            ColumnInfo columnInfo = columns.get(columnName.toLowerCase());
            
            if (columnInfo == null) continue;
            
            String escapedColumn = dialect.escapeKeyword(columnInfo.getColumn());
            
            // 根据参数类型判断条件类型
            WhereCondition condition;
            if (value == null) {
                condition = new NullCondition();
            } else if (value instanceof String && fieldName.startsWith("like_")) {
                // 特殊处理：字段名以"like_"开头的字符串参数生成LIKE条件
                condition = new LikeCondition(value);
            } else if (value.getClass().isArray() || value instanceof Collection) {
                condition = new ArrayCondition(value);
            } else {
                condition = new EqualsCondition(value);
            }
            
            String whereFragment = condition.buildWhereCondition(
                escapedColumn, fieldName, getAdditionalParams()
            );
            WHERE(whereFragment);
        }
    }
}
```

#### 步骤3：注册自定义Provider

```java
package com.example.mybatis.config;

import com.yixsoft.support.mybatis.autosql.configuration.InterfaceMapperConfigurator;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

public class LikeMapperConfigurator implements InterfaceMapperConfigurator {
    
    @Override
    public void config(MapperFactoryBean factory, Class mapperInterface, 
                       Class<? extends KeyGenerator> defaultKeyGen) {
        // 检查是否有需要使用LIKE查询的方法
        for (Method method : mapperInterface.getDeclaredMethods()) {
            if (hasLikeQueryAnnotation(method)) {  // 自定义注解判断
                // 创建自定义MappedStatement
                createLikeMappedStatement(factory, method);
            }
        }
    }
    
    private void createLikeMappedStatement(MapperFactoryBean factory, Method method) {
        // 创建AutoSqlSource并使用LikeSelectSqlProvider
        // ...
    }
}
```

#### 步骤4：注册配置器

```java
@Configuration
public class CustomProviderConfig {
    @PostConstruct
    public void registerConfigurator() {
        YixMapperFactoryBean.registerConfiguration(LikeMapperConfigurator.class);
    }
}
```

### Provider扩展要点

#### 继承AbstractSqlProvider

大部分场景只需继承`AbstractSqlProvider`并重写`buildSql()`或`buildWhereClause()`方法。

#### 使用MyBatis SQL类

继承`SQL`类可以使用Builder模式构建SQL：

```java
public class CustomSelectProvider extends AbstractSqlProvider {
    @Override
    protected String buildSql() {
        SELECT("*");  // 使用SQL类的Builder方法
        FROM(getTableName());
        buildWhereClause();
        return toString();  // 返回构建的SQL字符串
    }
}
```

#### 线程安全

Provider实例每次执行都会重新创建，内部状态线程隔离，无需考虑并发问题。

## InterfaceMapperConfigurator扩展

### 创建自定义配置器

用于扩展Mapper接口的扫描和配置逻辑。

#### 场景：自动注册手动SQL

假设需要自动扫描`@ManualSql`标注的方法并注册SQL。

```java
package com.example.mybatis.config;

import com.yixsoft.support.mybatis.autosql.configuration.InterfaceMapperConfigurator;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

public class ManualSqlConfigurator implements InterfaceMapperConfigurator {
    
    @Override
    public void config(MapperFactoryBean factory, Class mapperInterface, 
                       Class<? extends KeyGenerator> defaultKeyGen) {
        Configuration configuration = factory.getSqlSession().getConfiguration();
        
        for (Method method : mapperInterface.getDeclaredMethods()) {
            ManualSql manualSql = method.getAnnotation(ManualSql.class);
            if (manualSql != null && !configuration.hasStatement(getStatementName(method))) {
                // 创建MappedStatement
                MappedStatement ms = createMappedStatement(
                    configuration, method, manualSql
                );
                configuration.addMappedStatement(ms);
            }
        }
    }
    
    private MappedStatement createMappedStatement(Configuration config, 
                                                   Method method, ManualSql annotation) {
        // 根据annotation.value()创建SQL语句
        // ...
    }
}
```

### 配置器注册

```java
@Configuration
public class ConfiguratorRegistration {
    @PostConstruct
    public void register() {
        YixMapperFactoryBean.registerConfiguration(ManualSqlConfigurator.class);
    }
}
```

## WhereCondition扩展

### 创建自定义WHERE条件

用于支持特殊条件类型（如BETWEEN、OR等）。

#### 场景：BETWEEN条件

```java
package com.example.mybatis.where;

import com.yixsoft.support.mybatis.autosql.core.where.WhereCondition;

public class BetweenCondition implements WhereCondition {
    private final Object minValue;
    private final Object maxValue;
    
    public BetweenCondition(Object minValue, Object maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    @Override
    public String buildWhereCondition(String columnName, String key, 
                                      Map<String, Object> additionalParam) {
        String minKey = "__between_min_" + key;
        String maxKey = "__between_max_" + key;
        
        additionalParam.put(minKey, minValue);
        additionalParam.put(maxKey, maxValue);
        
        return columnName + " BETWEEN #{__between_min_" + key + "} AND #{__between_max_" + key + "}";
    }
}
```

#### 使用自定义条件

在Provider的`buildWhereClause()`中使用：

```java
@Override
protected void buildWhereClause() {
    for (Map.Entry<String, Object> entry : param.entrySet()) {
        String fieldName = entry.getKey();
        Object value = entry.getValue();
        
        // 特殊字段名判断
        if (fieldName.startsWith("between_")) {
            // 假设value是一个包含min和max的对象
            BetweenValue betweenValue = (BetweenValue) value;
            WhereCondition condition = new BetweenCondition(
                betweenValue.getMin(), betweenValue.getMax()
            );
            WHERE(condition.buildWhereCondition(...));
        }
    }
}
```

## 全局配置扩展

### 自定义配置属性

#### 步骤1：扩展YixMyBatisConfig

```java
package com.example.mybatis.config;

import com.yixsoft.support.mybatis.spring.YixMyBatisConfig;

public class ExtendedMyBatisConfig extends YixMyBatisConfig {
    private Boolean enableLikeQuery = false;  // 自定义属性
    private Boolean enableBetweenCondition = false;
    
    // getter/setter...
}
```

#### 步骤2：更新Spring Boot配置绑定

```java
@Configuration
public class MapperConfiguration {
    
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public ExtendedMyBatisConfig extendedMyBatisConfig() {
        return new ExtendedMyBatisConfig();
    }
}
```

#### 步骤3：配置文件中使用

```yaml
mybatis:
  enable-like-query: true
  enable-between-condition: true
  dialects:
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
```

## Spring Boot自动配置扩展

### 创建自定义自动配置

#### 场景：自动注册方言

```java
package com.example.mybatis.autoconfigure;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class, DataSource.class})
@AutoConfigureAfter(MapperConfiguration.class)
public class PostgreSqlDialectAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public PostgreSqlDialect postgreSqlDialect() {
        return new PostgreSqlDialect();
    }
    
    @PostConstruct
    public void registerDialect() {
        SqlDialectManager.register("postgresql", PostgreSqlDialect.class);
    }
}
```

#### 注册自动配置

在`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`中添加：

```
com.example.mybatis.autoconfigure.PostgreSqlDialectAutoConfiguration
```

## 扩展最佳实践

### 1. 模块化设计

将扩展功能作为独立模块发布：

```
mybatis-extension/
├── dialect-postgresql/
├── provider-advanced-query/
├── pk-custom-generator/
└── autoconfigure/
```

### 2. 注解驱动

使用注解标记扩展功能：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LikeQuery {
    boolean prefixMatch() default true;
    boolean suffixMatch() default true;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BetweenQuery {
    String minField();
    String maxField();
}
```

### 3. 配置开关

通过配置控制扩展功能启用：

```yaml
mybatis:
  extensions:
    like-query:
      enabled: true
    between-query:
      enabled: true
```

### 4. 向后兼容

扩展功能不应破坏原有功能：

```java
public class ExtendedSelectSqlProvider extends SelectSqlProvider {
    @Override
    protected void buildWhereClause() {
        super.buildWhereClause();  // 先执行原有逻辑
        
        // 再添加扩展逻辑
        if (config.isEnableLikeQuery()) {
            buildLikeClause();
        }
    }
}
```

### 5. 文档完善

为扩展功能提供完整文档：

- 功能说明
- 使用示例
- 配置说明
- 限制说明
- 性能影响

## 总结

YixSoft-Autobatis的扩展机制设计遵循开放封闭原则：

1. **方言扩展**：通过`ISqlDialect`接口支持新数据库
2. **主键生成器扩展**：通过`IPrimaryKeyProvider`接口支持新主键策略
3. **SQL Provider扩展**：通过继承`AbstractSqlProvider`支持新SQL类型
4. **配置器扩展**：通过`InterfaceMapperConfigurator`接口支持新Mapper配置逻辑
5. **WHERE条件扩展**：通过`WhereCondition`接口支持新条件类型
6. **Spring Boot扩展**：通过自动配置机制实现零配置启用

合理使用扩展机制，可以在不修改核心代码的情况下满足各种定制化需求。