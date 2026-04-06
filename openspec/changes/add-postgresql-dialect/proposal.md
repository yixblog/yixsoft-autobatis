## Why

当前yixsoft-autobatis项目已经支持MySQL和Oracle方言，但缺少对PostgreSQL数据库的支持。PostgreSQL作为一款功能强大、开源的关系型数据库，在企业级应用中使用广泛。添加PostgreSQL方言支持将扩展项目的数据库兼容性，满足更多用户的需求，并提升项目的市场竞争力。

## What Changes

- **版本升级**: 将项目主版本号从2.0.5升级到2.1.0，以反映新增的重要功能
- **新增模块**: 创建新的Maven模块 `yixsoft-batis-dialect-pg`（使用pg作为postgresql的缩写）
- **方言实现**: 实现PostgreSQL专用的ISqlDialect接口，包括：
  - 表结构查询功能（通过information_schema）
  - 关键字转义（使用双引号）
  - ROW_NUMBER窗口函数支持（需自己实现）
  - PostgreSQL特有的数据类型映射
- **分页支持**: 复用现有mybatis-paginator第三方插件，已内置PostgreSQLDialect支持
- **自动配置**: 更新方言管理器以自动识别和加载PostgreSQL方言
- **文档更新**: 更新项目文档以包含PostgreSQL方言的使用说明

## Capabilities

### New Capabilities
- `postgresql-dialect`: PostgreSQL数据库方言支持，提供完整的SQL生成适配能力，包括表结构查询、关键字转义、分页处理等功能

### Modified Capabilities
- `dialect-system`: 方言系统需要支持新的PostgreSQL方言注册和自动发现机制

## Impact

- **核心模块**: yixsoft-batis-dialects-core模块需要确保方言管理器能够正确加载PostgreSQL方言
- **构建配置**: pom.xml文件需要添加新的子模块 `yixsoft-batis-dialect-pg`
- **版本管理**: 项目版本号需要从2.0.5升级到2.1.0
- **依赖管理**: PostgreSQL方言模块需要添加PostgreSQL JDBC驱动作为可选依赖
- **测试覆盖**: 需要为PostgreSQL方言添加相应的单元测试和集成测试
- **文档**: 需要更新用户文档以包含PostgreSQL方言的配置和使用说明