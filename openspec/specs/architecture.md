# 架构设计

## 整体架构

YixSoft-Autobatis 采用分层架构设计，核心分为以下层次：

```
┌─────────────────────────────────────────────────────────┐
│              Spring Boot应用层                           │
│  (MapperScanner, DataSource, MyBatis Configuration)    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          Spring Boot自动配置层                           │
│  (MapperConfiguration, YixMyBatisConfig)                │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          MyBatis集成层                                   │
│  (YixMapperFactoryBean, AutoSqlMapperConfigurator)      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          SQL生成引擎层                                   │
│  (AutoSqlSource, SqlGenerationConfig, IAutoSqlProvider) │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          方言系统层                                      │
│  (ISqlDialect, SqlDialectManager, TableStructureUtils) │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          数据库层                                        │
│  (MySQL, Oracle, ...)                                   │
└─────────────────────────────────────────────────────────┘
```

## 核心组件职责

### 1. Spring Boot自动配置层

#### MapperConfiguration

```java
@Configuration
public class MapperConfiguration {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, YixMyBatisConfig config) {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        
        // 关键：设置自定义MapperFactoryBean
        YixMapperFactoryBean.setDefaultKeyGen(config.getDefaultKeyGenerator());
        
        // 注册方言
        registerDialects(config.getDialects());
        
        return factory.getObject();
    }
}
```

**职责**：
- 替换默认的MyBatis配置，使用自定义`YixMapperFactoryBean`
- 绑定配置属性到`YixMyBatisConfig`
- 注册数据库方言到方言管理器

#### YixMyBatisConfig

```java
public class YixMyBatisConfig {
    private Class<? extends KeyGenerator> defaultKeyGenerator;
    private Class<? extends ISqlDialect>[] dialects;
    private boolean enableStrictTableCheck;
    private PaginatorConfig paginator;
}
```

**职责**：
- 接收Spring Boot配置属性（`mybatis.*`前缀）
- 初始化时自动注册方言
- 存储全局配置供运行时使用

### 2. MyBatis集成层

#### YixMapperFactoryBean

继承MyBatis-Spring的`MapperFactoryBean`，在Mapper接口初始化时注入自动SQL逻辑。

```java
public class YixMapperFactoryBean<T> extends MapperFactoryBean<T> 
    implements IAutoSqlFactoryBean {
    
    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        attachAutoSqlStatements();  // 核心：自动注册SQL语句
    }
    
    @Override
    public void attachAutoSqlStatements() {
        for (InterfaceMapperConfigurator configurator : configurators) {
            configurator.config(this, getMapperInterface(), defaultKeyGen);
        }
    }
}
```

**职责**：
- 扩展MyBatis Mapper创建流程
- 调用配置器扫描并注册自动SQL
- 提供方言获取接口（通过数据库连接获取产品名）

#### AutoSqlMapperConfigurator

核心配置器，扫描Mapper接口并注册MappedStatement。

```java
public class AutoSqlMapperConfigurator implements InterfaceMapperConfigurator {
    @Override
    public void config(MapperFactoryBean factory, Class mapperInterface, 
                       Class<? extends KeyGenerator> defaultKeyGen) {
        if (!hasAutoMapperAnnotation(mapperInterface)) return;
        
        Configuration configuration = factory.getSqlSession().getConfiguration();
        
        for (Method method : mapperInterface.getDeclaredMethods()) {
            if (hasAutoSqlAnnotation(method)) {
                String statementName = getStatementName(mapperInterface, method);
                
                if (!configuration.hasStatement(statementName)) {
                    AutoSqlSource sqlSource = createSqlSource(factory, method, defaultKeyGen);
                    MappedStatement ms = sqlSource.createStatement();
                    configuration.addMappedStatement(ms);
                }
            }
        }
    }
}
```

**职责**：
- 扫描`@AutoMapper`标注的接口
- 扫描`@AutoSql`标注的方法
- 创建`AutoSqlSource`并注册MappedStatement
- 处理主键生成器配置

### 3. SQL生成引擎层

#### AutoSqlSource

动态SQL源，每次执行时根据参数动态生成SQL。

```java
public class AutoSqlSource implements SqlSource {
    private final SqlGenerationConfig genConfig;
    
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        // 1. 根据参数创建Provider并生成SQL
        IAutoSqlProvider provider = genConfig.newSqlProvider(parameterObject);
        
        // 2. 创建临时SqlSource
        SqlSource sqlSource = new DynamicSqlSource(provider.getSql());
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        
        // 3. 设置额外参数（IN条件展开等）
        provider.getAdditionalParams().forEach(boundSql::setAdditionalParameter);
        
        return boundSql;
    }
}
```

**职责**：
- 实现`SqlSource`接口，集成到MyBatis执行流程
- 根据参数动态生成SQL
- 处理额外参数注入（IN条件参数展开）

#### SqlGenerationConfig

SQL生成配置容器，解析注解并存储生成所需的所有信息。

```java
public class SqlGenerationConfig {
    private SqlType type;                    // INSERT/UPDATE/SELECT/DELETE/COUNT
    private String tableName;                // 表名
    private String[] pkNames;                // 主键列名
    private ISqlDialect dialect;             // 数据库方言
    private Class<? extends KeyGenerator> pkProvider;  // 主键生成器
    private IgnoreNullRule ignoreNullRule;  // null值处理规则
    
    public SqlGenerationConfig(MapperFactoryBean factory, Method method, ...) {
        // 1. 解析方法上的@AutoSql注解
        AutoSql autoSql = getMergedAnnotation(method, AutoSql.class);
        this.type = autoSql.value()[0];
        this.ignoreNullRule = autoSql.ignoreNullRule();
        
        // 2. 解析接口上的@AutoMapper注解
        AutoMapper autoMapper = getMergedAnnotation(method.getDeclaringClass(), AutoMapper.class);
        this.tableName = autoMapper.tablename();
        this.pkNames = autoMapper.pkName();
        
        // 3. 获取数据库方言
        DatabaseMetaData metaData = factory.getDataSource().getConnection().getMetaData();
        this.dialect = SqlDialectManager.getDialect(factory, metaData.getDatabaseProductName());
        
        // 4. 解析扩展注解（@AdvanceSelect等）
        parseAdvanceAnnotations(method);
    }
    
    public IAutoSqlProvider newSqlProvider(Object parameterObject) {
        // 1. 获取表结构（带缓存）
        Map<String, ColumnInfo> tableColumns = TableStructureUtils.getTableColumns(dialect, tableName);
        
        // 2. 创建对应类型的Provider
        IAutoSqlProvider provider = type.getProviderClass().newInstance();
        
        // 3. 注入所有配置
        provider.setTable(tableName);
        provider.setTableColumns(tableColumns);
        provider.setDialect(dialect);
        provider.setParameter(parameterObject);
        provider.setPkNames(pkNames);
        provider.setConfiguration(configuration);
        
        return provider;
    }
}
```

**职责**：
- 解析注解配置，构建生成上下文
- 获取数据库方言（运行时动态选择）
- 创建SQL Provider实例并注入配置

#### IAutoSqlProvider接口体系

```java
public interface IAutoSqlProvider {
    String getSql();                            // 生成的SQL语句
    Map<String, Object> getAdditionalParams();  // 额外参数（IN条件等）
    void setParameter(Object parameterObject);  // 设置参数
    void setTableColumns(Map<String, ColumnInfo> columns);  // 表结构
    void setDialect(ISqlDialect dialect);      // 数据库方言
    void setConfiguration(Configuration config); // MyBatis配置
    // ... 其他setter方法
}
```

**设计特点**：
- 使用Builder模式（继承MyBatis的`SQL`类）
- Provider实例化后不可变（线程安全）
- 每次SQL执行创建新Provider实例

### 4. 方言系统层

#### ISqlDialect接口

```java
public interface ISqlDialect {
    void init(MapperFactoryBean parentFactory);  // 初始化方言
    
    List<ColumnInfo> selectTableColumns(String tableName);  // 获取表列信息
    
    String escapeKeyword(String name);  // 关键字转义
    
    String rowNumberFragment(RowNumberConfig config);  // row_number片段
}
```

**职责**：
- 定义方言核心行为
- 封装数据库特性差异

#### SqlDialectManager

```java
public class SqlDialectManager {
    private static final Map<String, Class<? extends ISqlDialect>> dialectMap = new HashMap<>();
    
    public static void register(String dialectName, Class<? extends ISqlDialect> dialectClass) {
        dialectMap.put(dialectName.toLowerCase(), dialectClass);
    }
    
    public static ISqlDialect getDialect(MapperFactoryBean factory, String dbName) {
        Class<? extends ISqlDialect> clazz = dialectMap.get(dbName.toLowerCase());
        ISqlDialect dialect = BeanUtils.instantiateClass(clazz);
        dialect.init(factory);
        return dialect;
    }
}
```

**职责**：
- 维护方言注册表
- 根据数据库产品名动态获取方言实例
- 初始化方言（注入MapperFactoryBean以便访问数据源）

#### TableStructureUtils

```java
public class TableStructureUtils {
    private static final Map<String, Map<String, ColumnInfo>> tableStructureCache = new HashMap<>();
    
    public static Map<String, ColumnInfo> getTableColumns(ISqlDialect dialect, String tableName) {
        // 双重检查锁定的缓存机制
        if (!tableStructureCache.containsKey(tableName)) {
            synchronized (tableStructureCache) {
                if (!tableStructureCache.containsKey(tableName)) {
                    Map<String, ColumnInfo> structure = dialect.selectTableColumns(tableName);
                    tableStructureCache.put(tableName, structure);
                }
            }
        }
        return new HashMap<>(tableStructureCache.get(tableName));
    }
}
```

**职责**：
- 缓存表结构信息
- 首次访问时调用方言获取表结构
- 提供线程安全的缓存访问

## 关键设计模式

### 1. 策略模式 - SQL生成

通过`IAutoSqlProvider`接口和不同Provider实现，支持不同SQL类型的生成策略。

```java
SqlType enum → Provider Class mapping:
  INSERT  → InsertSqlProvider
  UPDATE  → UpdateSqlProvider
  SELECT  → SelectSqlProvider
  DELETE  → DeleteSqlProvider
  COUNT   → CountSqlProvider
```

### 2. 策略模式 - WHERE条件构建

```java
WhereCondition interface:
  EqualsCondition    → column = #{param}
  ArrayCondition     → column IN (?, ?, ?)
  NullCondition      → column IS NULL
  CustomCondition    → 自定义条件片段
```

### 3. 工厂模式 - Provider创建

`SqlGenerationConfig.newSqlProvider()`根据SqlType动态创建对应Provider实例。

### 4. 模板方法模式 - AbstractSqlProvider

```java
public abstract class AbstractSqlProvider extends SQL implements IAutoSqlProvider {
    @Override
    public String getSql() {
        if (builtSql == null) {
            builtSql = buildSql();  // 模板方法
        }
        return builtSql;
    }
    
    protected abstract String buildSql();  // 子类实现
}
```

### 5. 注册表模式 - 方言管理

`SqlDialectManager`维护方言注册表，通过数据库产品名查找对应方言。

### 6. 缓存模式 - 表结构缓存

`TableStructureUtils`使用静态Map缓存表结构，避免重复查询数据库元数据。

## 数据流

### Mapper接口初始化流程

```
1. Spring扫描Mapper接口 (@MapperScanner)
   ↓
2. 创建YixMapperFactoryBean实例
   ↓
3. 调用checkDaoConfig()
   ↓
4. AutoSqlMapperConfigurator扫描接口
   ↓
5. 为每个@AutoSql方法创建AutoSqlSource
   ↓
6. SqlGenerationConfig解析注解配置
   ↓
7. AutoSqlSource.createStatement()
   ↓
8. 注册MappedStatement到Configuration
```

### SQL执行流程

```
1. MyBatis调用MappedStatement
   ↓
2. AutoSqlSource.getBoundSql(parameterObject)
   ↓
3. SqlGenerationConfig.newSqlProvider(parameterObject)
   ↓
4. TableStructureUtils.getTableColumns(dialect, tableName)
   ↓
5. IAutoSqlProvider.setParameter(parameterObject)
   ↓
6. IAutoSqlProvider.buildSql()
   ↓
7. 创建BoundSql并注入额外参数
   ↓
8. MyBatis执行SQL
```

## 扩展点设计

### 1. 方言扩展

新增数据库方言只需：

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
```

### 2. SQL Provider扩展

可注册新的InterfaceMapperConfigurator实现其他类型SQL生成。

### 3. 主键生成器扩展

实现`IPrimaryKeyProvider`接口或继承`AbstractKeyProvider`。

## 性能考虑

### 缓存机制

1. **表结构缓存**：首次查询后缓存，避免重复访问数据库元数据
2. **类描述缓存**：JavaBean字段描述缓存，避免重复反射解析
3. **SQL缓存**：Provider实例内部缓存生成的SQL（同一参数不重新生成）

### 线程安全

1. **Provider实例**：每次执行创建新实例，线程隔离
2. **表结构缓存**：双重检查锁定保护
3. **方言实例**：每次获取创建新实例，避免共享状态

### 连接使用

方言仅在首次访问表结构时使用数据库连接，后续使用缓存，不影响业务连接池。

## 设计权衡

### 优点

1. **零SQL编写**：减少大量重复工作
2. **动态表结构**：自动适应表结构变化
3. **方言抽象**：统一多数据库使用体验
4. **Spring Boot集成**：开箱即用

### 局限性

1. **单表限制**：不支持复杂JOIN查询
2. **性能损耗**：首次执行需要查询表结构
3. **灵活性受限**：复杂业务逻辑仍需手动SQL
4. **调试困难**：动态生成SQL不易调试

## 架构演进考虑

### 潜在优化方向

1. **表结构缓存过期策略**：当前永久缓存，可考虑定时刷新
2. **SQL生成性能优化**：可预生成SQL模板，参数化处理
3. **复杂查询支持**：可考虑支持简单的JOIN注解
4. **异步表结构加载**：应用启动时预加载表结构

### 模块化改进

1. **方言独立包**：方言可完全独立发布，按需引入
2. **Provider可插拔**：可通过SPI机制加载自定义Provider
3. **配置中心集成**：支持从配置中心动态加载配置

## 总结

YixSoft-Autobatis架构设计遵循以下原则：

1. **关注点分离**：配置层、集成层、生成层、方言层职责清晰
2. **开放封闭原则**：通过接口和注册机制支持扩展，核心逻辑稳定
3. **单一职责**：每个组件职责单一，易于理解和维护
4. **依赖倒置**：高层模块依赖抽象接口，具体实现可替换

架构设计既保证了核心功能的稳定性，又为未来扩展留出了充分空间。