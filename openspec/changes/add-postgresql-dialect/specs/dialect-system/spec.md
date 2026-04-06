# 方言系统规范（修改）

## MODIFIED Requirements

### Requirement: 方言自动发现和注册机制

方言系统 SHALL 支持通过@SupportsDatabase注解自动发现和注册方言实例，无需手动配置。

#### Scenario: 注解驱动的方言注册

- **WHEN** 方言类标注@SupportsDatabase注解并指定数据库类型名称
- **THEN** SqlDialectManager扫描并自动注册该方言到对应的数据库类型映射表中

#### Scenario: 多数据库类型支持

- **WHEN** 方言类支持多种数据库类型（如MySQL方言同时支持mysql和mariadb）
- **THEN** @SupportsDatabase注解接受数组参数，SqlDialectManager为每种类型注册同一方言实例

#### Scenario: Spring Boot自动扫描

- **WHEN** Spring Boot应用启动时扫描dialects配置包路径
- **THEN** 自动发现所有标注@SupportsDatabase的方言类并注册到SqlDialectManager

### Requirement: 方言管理器扩展能力

SqlDialectManager SHALL 支持动态注册新方言，允许第三方方言模块扩展系统功能。

#### Scenario: 手动注册方言

- **WHEN** 用户通过代码调用SqlDialectManager.register方法
- **THEN** 方言被正确注册，后续SQL生成操作可以使用该方言

#### Scenario: 方言优先级处理

- **WHEN** 多个方言支持同一数据库类型
- **THEN** 使用最后注册的方言作为该类型的默认方言

#### Scenario: 方言实例缓存

- **WHEN** 方言被首次加载并初始化
- **THEN** SqlDialectManager缓存方言实例，避免重复创建和初始化

### Requirement: 方言配置集成

方言系统 SHALL 与Spring Boot配置系统深度集成，支持通过配置文件指定方言。

#### Scenario: 配置文件指定方言

- **WHEN** 用户在application.yml中配置mybatis.dialects属性
- **THEN** MapperConfiguration加载指定的方言类并注册到SqlDialectManager

#### Scenario: 方言与分页插件集成

- **WHEN** 配置方言和分页插件时
- **THEN** 系统自动配置mybatis-paginator对应的方言类，确保分页功能正确工作

#### Scenario: PostgreSQL方言与paginator集成

- **WHEN** 配置PostgreSQL方言时
- **THEN** PaginatorConfig的dialect属性自动设置为"com.github.miemiedev.mybatis.paginator.dialect.PostgreSQLDialect"