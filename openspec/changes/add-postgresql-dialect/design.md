## Context

当前项目已实现MySQL和Oracle两种数据库方言，遵循统一的`ISqlDialect`接口规范。方言系统采用注解驱动设计，通过`@SupportsDatabase`注解声明支持的数据库类型，由`SqlDialectManager`自动发现和注册。

PostgreSQL作为主流开源关系型数据库，在企业级应用中使用广泛。添加PostgreSQL方言支持将扩展项目的数据库兼容性范围，满足更多用户的数据库适配需求。

### 现有架构分析

方言模块遵循以下架构模式：

```
yixsoft-batis-dialect-{db}/
├── pom.xml                          # Maven模块定义
├── src/main/java/
│   └── dialect包/
│       ├── {DB}Dialect.java         # 方言实现类
│       ├── mappers/
│       │   ├── {DB}TableMapper.java # 表结构查询Mapper
│       │   └── {DB}ColumnDAO.java   # 列信息DAO
```

方言实现类需要：
1. 标注`@SupportsDatabase`注解声明数据库类型
2. 实现`ISqlDialect`接口的4个方法
3. 通过内部Mapper查询表结构元数据
4. 处理数据库特有的数据类型映射

## Goals / Non-Goals

**Goals:**
- 创建PostgreSQL方言模块`yixsoft-batis-dialect-pg`
- 实现完整的`ISqlDialect`接口功能
- 支持PostgreSQL特有数据类型（JSON、JSONB、UUID等）
- 集成mybatis-paginator的PostgreSQL分页支持
- 项目版本升级到2.1.0

**Non-Goals:**
- 不修改现有MySQL/Oracle方言实现
- 不改变核心方言系统架构
- 不添加PostgreSQL特有的SQL语法特性（如数组查询等）
- 不实现PostgreSQL存储过程或函数支持

## Decisions

### Decision 1: 模块命名

**选择**: `yixsoft-batis-dialect-pg`（使用pg缩写）

**理由**:
- PostgreSQL名称过长（11字符），使用标准缩写pg（2字符）
- pg是PostgreSQL官方文档和社区的标准缩写形式
- 与现有模块命名规范保持一致（mysql、oracle）

**替代方案**:
- `yixsoft-batis-dialect-postgresql` - 名称过长，不符合现有模式
- `yixsoft-batis-dialect-postgres` - 虽然常用，但不如pg简洁

### Decision 2: 表结构查询方式

**选择**: 使用`information_schema.columns`查询表结构

**理由**:
- PostgreSQL完全支持ANSI标准的information_schema
- 与MySQL方言实现方式一致，便于维护
- 查询结果字段标准化，易于转换为ColumnInfo

**SQL查询模板**:
```sql
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default,
    character_maximum_length,
    numeric_precision,
    numeric_scale
FROM information_schema.columns
WHERE table_name = #{tableName}
ORDER BY ordinal_position
```

**替代方案**:
- 使用pg_catalog系统表 - PostgreSQL原生方式，但查询复杂度高
- 使用DESC命令 - PostgreSQL不支持，MySQL特有

### Decision 3: 数据类型映射策略

**选择**: 映射到标准JDBC类型，特殊类型使用VARCHAR

**理由**:
- 标准类型确保MyBatis兼容性
- JSON/JSONB/UUID等特殊类型映射为VARCHAR，用户可自定义TypeHandler
- 避免引入额外的PostgreSQL JDBC驱动强依赖

**映射规则**:
| PostgreSQL类型 | JDBC类型 |
|----------------|----------|
| integer, bigint, smallint | INTEGER, BIGINT, SMALLINT |
| varchar, text, char | VARCHAR, LONGVARCHAR, CHAR |
| boolean | BOOLEAN |
| timestamp, date, time | TIMESTAMP, DATE, TIME |
| numeric, decimal | NUMERIC, DECIMAL |
| json, jsonb | VARCHAR (用户自定义处理) |
| uuid | VARCHAR (用户自定义处理) |
| bytea | BINARY |

**替代方案**:
- 使用PostgreSQL JDBC驱动的专有类型 - 需要强依赖，增加模块复杂度
- 实现自定义TypeHandler - 超出方言模块职责范围

### Decision 4: 关键字转义方式

**选择**: 使用双引号转义（`"`name`"`）

**理由**:
- PostgreSQL标准转义方式
- 与Oracle方言一致，代码可复用
- 符合ANSI SQL标准

**实现**:
```java
@Override
public String escapeKeyword(String name) {
    return "\"" + name + "\"";
}
```

### Decision 5: ROW_NUMBER实现

**选择**: 实现标准row_number() over()语法

**理由**:
- PostgreSQL完全支持SQL:2003标准的窗口函数
- 与MySQL 8.0+语法一致
- 实现简单，直接返回格式化字符串

**实现**:
```java
@Override
public String rowNumberFragment(RowNumberConfig config) {
    return "row_number() over (%s) as %s"
        .formatted(config.over(), config.asColumn());
}
```

### Decision 6: 分页支持集成

**选择**: 复用mybatis-paginator的PostgreSQLDialect

**理由**:
- 项目已依赖mybatis-paginator插件
- 该插件内置PostgreSQLDialect，无需重复实现
- 仅需在配置中正确设置PaginatorConfig.dialect

**配置示例**:
```java
PaginatorConfig paginatorConfig = new PaginatorConfig()
    .setDialect("com.github.miemiedev.mybatis.paginator.dialect.PostgreSQLDialect");
```

### Decision 7: 版本号升级策略

**选择**: 从2.0.5升级到2.1.0（minor版本升级）

**理由**:
- 新增功能属于功能性扩展，不破坏现有接口
- 遵循语义化版本规范（SemVer）：新功能 → minor版本
- 与上一版本2.0.x系列保持兼容性

**替代方案**:
- 升级到3.0.0 - 过于激进，不符合变更影响范围
- 保持2.0.5，仅添加revision suffix - 不利于版本管理

### Decision 8: Auto-increment主键回填兼容性

**选择**: 无需方言层额外处理，复用MyBatis标准机制

**理由**:
- 项目使用MyBatis标准的`Jdbc3KeyGenerator`处理auto-increment主键回填
- `Jdbc3KeyGenerator`使用JDBC标准的`Statement.getGeneratedKeys()`方法
- PostgreSQL JDBC驱动完全支持此标准API，无需方言层干预
- 与MySQL/Oracle方言保持一致的用户体验和配置方式

**实现方式**:
- 用户在`@AutoMapper`注解中指定`keyGenerator = Jdbc3KeyGenerator.class`
- PostgreSQL表使用SERIAL/BIGSERIAL类型定义自增列
- INSERT执行后，MyBatis自动回填主键值到参数对象
- 方言层仅负责SQL生成，不参与主键回填逻辑

**兼容性验证**:
- 项目现有测试已验证auto-increment主键回填功能（TestBatisPlugin.java）
- PostgreSQL JDBC驱动42.x版本完全支持getGeneratedKeys()
- 无需新增方言特有代码，保持架构简洁

## Risks / Trade-offs

### Risk 1: PostgreSQL数据类型复杂性

**风险**: PostgreSQL支持丰富的数据类型（数组、复合类型、范围类型等），可能导致类型映射不完整

**缓解**: 
- 方言仅处理常见基础类型映射
- 文档说明特殊类型需用户自定义TypeHandler
- 提供类型扩展指南示例

### Risk 2: 方言注册时机

**风险**: PostgreSQL方言需在应用启动前注册，否则可能导致首次SQL生成失败

**缓解**:
- 使用Spring Boot自动配置机制，确保启动时注册
- MapperConfiguration扫描所有标注@SupportsDatabase的方言类
- 提供手动注册兜底方案

### Risk 3: 模块依赖冲突

**风险**: PostgreSQL JDBC驱动版本可能与用户项目版本冲突

**缓解**:
- pom.xml中PostgreSQL JDBC驱动设置为`provided` scope
- 用户自行管理驱动版本，避免冲突
- 文档明确说明依赖管理策略

### Risk 4: 表结构查询性能

**风险**: information_schema查询可能较慢，影响首次Mapper初始化性能

**缓解**:
- SqlDialectManager缓存方言实例，避免重复初始化
- 表结构信息仅首次查询，后续复用
- 提供文档说明性能特性

### Trade-off 1: 类型映射简化 vs 完整性

**权衡**: 为保证兼容性，简化特殊类型映射（如UUID→VARCHAR）

**代价**: 用户需额外实现TypeHandler处理特殊类型

**收益**: 模块简洁，降低维护成本，避免JDBC驱动强依赖

### Trade-off 2: 标准语法 vs PostgreSQL特性

**权衡**: 仅实现标准SQL语法，不支持PostgreSQL特有语法（如RETURNING、数组查询）

**代价**: 无法利用PostgreSQL性能优化特性

**收益**: 保持方言系统一致性，便于未来扩展其他数据库

## Migration Plan

### 部署步骤

1. **发布新版本**
   - 发布yixsoft-batis-parent 2.1.0
   - 发布yixsoft-batis-dialect-pg 2.1.0

2. **用户迁移**
   - 在pom.xml中添加PostgreSQL方言依赖：
     ```xml
     <dependency>
         <groupId>com.yixsoft</groupId>
         <artifactId>yixsoft-batis-dialect-pg</artifactId>
         <version>2.1.0</version>
     </dependency>
     ```
   - 配置方言：
     ```yaml
     mybatis:
       dialects:
         - com.yixsoft.support.mybatis.dialects.pg.PostgreSqlDialect
     ```

3. **数据库切换**
   - 确保PostgreSQL JDBC驱动已配置
   - 应用启动后自动识别PostgreSQL方言

### 回滚策略

- 如发现兼容性问题，用户可回退到2.0.5版本
- PostgreSQL方言模块独立，不影响现有MySQL/Oracle方言
- 移除PostgreSQL方言依赖即可回退

### 兼容性保证

- 保持ISqlDialect接口不变
- SqlDialectManager注册机制不变
- Spring Boot配置方式不变
- 现有MySQL/Oracle方言功能不受影响

## Open Questions

### Q1: 是否需要支持PostgreSQL的SERIAL类型？

**现状**: PostgreSQL的SERIAL/BIGSERIAL类型实际上是integer/bigint + auto-increment sequence的组合

**决策**: 是否在ColumnInfo中标记为auto-increment类型，支持主键自动生成策略识别？

**建议**: 暂不实现，等待用户需求反馈

### Q2: 是否需要添加PostgreSQL特有的Schema支持？

**现状**: PostgreSQL支持多Schema（类似数据库的概念），查询表结构可能需要指定Schema名称

**决策**: selectTableColumns方法是否需要支持schema.table_name格式？

**建议**: 当前仅支持table_name，如需要Schema支持，可扩展接口方法

### Q3: 是否需要实现PostgreSQL的RETURNING语法？

**现状**: PostgreSQL INSERT支持RETURNING clause，可返回插入的数据，MySQL不支持

**决策**: 是否扩展INSERT SQL生成支持RETURNING clause？

**建议**: 暂不实现，超出当前方言系统职责范围，可作为未来特性

### Q4: mybatis-paginator的PostgreSQLDialect是否足够？

**现状**: mybatis-paginator内置PostgreSQLDialect使用LIMIT/OFFSET语法

**决策**: 是否需要验证分页功能在所有PostgreSQL版本上正常工作？

**建议**: 有条件时测试PostgreSQL 18版本兼容性