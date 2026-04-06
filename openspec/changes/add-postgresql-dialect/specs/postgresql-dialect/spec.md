# PostgreSQL方言规范

## ADDED Requirements

### Requirement: PostgreSQL数据库方言支持

系统 SHALL 提供PostgreSQL数据库方言实现，支持PostgreSQL数据库的SQL自动生成功能。

#### Scenario: 方言自动识别

- **WHEN** 应用连接到PostgreSQL数据库
- **THEN** SqlDialectManager自动识别并加载PostgreSQLDialect方言实例

#### Scenario: 方言手动配置

- **WHEN** 用户在配置文件中指定PostgreSQL方言
- **THEN** 系统加载PostgreSQLDialect方言并应用于所有AutoMapper

### Requirement: 表结构查询功能

PostgreSQL方言 SHALL 实现表结构查询功能，能够获取表的列信息、数据类型和约束信息。

#### Scenario: 查询表列信息

- **WHEN** 调用selectTableColumns方法查询表结构
- **THEN** 返回包含列名、JDBC类型、是否允许NULL等完整信息的ColumnInfo列表

#### Scenario: 查询不存在表的处理

- **WHEN** 查询不存在的表名
- **THEN** 返回空列表或抛出明确的异常信息

#### Scenario: 特殊数据类型映射

- **WHEN** 表包含PostgreSQL特有数据类型（如JSON、JSONB、UUID等）
- **THEN** 正确映射到对应的JDBC类型或自定义类型处理器

### Requirement: 关键字转义功能

PostgreSQL方言 SHALL 实现关键字转义功能，使用PostgreSQL标准的双引号转义机制。

#### Scenario: 转义保留关键字

- **WHEN** 表名或列名是PostgreSQL保留关键字（如order、group等）
- **THEN** escapeKeyword方法返回双引号包裹的名称（如"order"、"group"）

#### Scenario: 转义普通名称

- **WHEN** 表名或列名不是关键字
- **THEN** escapeKeyword方法返回双引号包裹的名称，保证一致性

#### Scenario: 转义包含特殊字符的名称

- **WHEN** 名称包含空格或特殊字符
- **THEN** escapeKeyword方法正确转义，确保SQL语法正确

### Requirement: ROW_NUMBER窗口函数支持

PostgreSQL方言 SHALL 实现ROW_NUMBER窗口函数支持，提供标准的row_number() over()语法片段。

#### Scenario: 生成ROW_NUMBER片段

- **WHEN** 调用rowNumberFragment方法并传入RowNumberConfig配置
- **THEN** 返回"row_number() over ({over_clause}) as {as_column}"格式的SQL片段

#### Scenario: 使用ROW_NUMBER进行排序查询

- **WHEN** 查询需要使用ROW_NUMBER进行分页或排序
- **THEN** 生成的SQL包含正确的row_number窗口函数语法

### Requirement: PostgreSQL JDBC驱动集成

PostgreSQL方言模块 SHALL 正确集成PostgreSQL JDBC驱动，支持PostgreSQL特有数据类型的处理。

#### Scenario: JDBC驱动依赖

- **WHEN** 用户引入PostgreSQL方言模块
- **THEN** 模块pom.xml包含PostgreSQL JDBC驱动作为依赖（scope为provided或optional）

#### Scenario: 数据类型识别

- **WHEN** PostgreSQL返回特有数据类型（如json、uuid等）
- **THEN** 方言正确识别并映射到Java类型

### Requirement: Maven模块独立发布

PostgreSQL方言 SHALL 作为独立的Maven模块发布，遵循项目模块命名规范。

#### Scenario: 模块命名规范

- **WHEN** 创建PostgreSQL方言模块
- **THEN** 模块artifactId为yixsoft-batis-dialect-pg（使用pg作为postgresql缩写）

#### Scenario: 模块依赖关系

- **WHEN** 构建PostgreSQL方言模块
- **THEN** 模块正确依赖yixsoft-batis-dialects-core核心模块

#### Scenario: 模块版本管理

- **WHEN** 发布PostgreSQL方言模块
- **THEN** 模块版本号与父项目版本号一致（使用${revision}属性）

### Requirement: Spring Boot自动配置支持

PostgreSQL方言 SHALL 支持Spring Boot自动配置，实现零配置启用。

#### Scenario: 自动发现方言

- **WHEN** Spring Boot应用启动并引入PostgreSQL方言模块
- **THEN** MapperConfiguration自动扫描并注册PostgreSQLDialect

#### Scenario: 条件化加载

- **WHEN** 配置中指定方言为postgresql
- **THEN** 系统自动加载PostgreSQLDialect并配置mybatis-paginator的PostgreSQLDialect

### Requirement: 兼容现有方言系统

PostgreSQL方言 SHALL 完全兼容现有的方言系统架构，不破坏现有功能。

#### Scenario: 方言注册机制

- **WHEN** PostgreSQLDialect实现ISqlDialect接口并标注@SupportsDatabase注解
- **THEN** SqlDialectManager正确识别并注册方言

#### Scenario: 方言切换

- **WHEN** 应用同时配置多个方言（MySQL、Oracle、PostgreSQL）
- **THEN** 系统根据数据库连接自动选择正确的方言实例