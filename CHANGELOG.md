# 更新日志 (Changelog)

本文档记录YixSoft-Autobatis项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [2.1.0] - 2025-04-06

### 新增 (Added)

- **PostgreSQL方言支持**：新增`yixsoft-batis-dialect-pg`模块，完整支持PostgreSQL数据库
  - 自动表结构查询（通过`information_schema`）
  - 关键字转义（使用双引号）
  - ROW_NUMBER窗口函数支持
  - PostgreSQL数据类型映射（支持JSON、JSONB、UUID等特有类型）
  - 分页查询支持（集成mybatis-paginator的PostgreSQLDialect）
  - Auto-increment主键回填支持（BIGSERIAL/SERIAL类型）

- **PostgreSQL方言文档**：
  - [PostgreSQL方言配置指南](docs/postgresql-dialect-guide.md)
  - [PostgreSQL数据类型映射](docs/postgresql-type-mapping.md)

### 变更 (Changed)

- **版本升级**：项目版本从2.0.5升级到2.1.0
- **多数据库支持说明**：更新README.md和文档，添加PostgreSQL支持说明

### 兼容性 (Compatibility)

- ✅ 完全向后兼容2.0.x版本
- ✅ 不影响现有MySQL和Oracle方言功能
- ✅ 保持所有现有API和配置方式不变

## [2.0.5] - 2024-XX-XX

### 变更 (Changed)

- 更新依赖版本
- 性能优化和bug修复

## [2.0.0] - 2024-XX-XX

### 新增 (Added)

- **重大升级**：
  - 升级到Java 17
  - 升级到Spring Boot 3.2+
  - 升级到MyBatis 3.5+

### 变更 (Changed)

- 重构核心架构
- 改进SQL生成引擎
- 优化方言系统

## [1.x.x] - 历史版本

历史版本记录请参阅Git提交历史。

---

## 版本命名规则

- **主版本号（Major）**：不兼容的API变更
- **次版本号（Minor）**：向后兼容的功能新增
- **修订号（Patch）**：向后兼容的问题修复

## 升级指南

### 从2.0.5升级到2.1.0

1. 更新Maven依赖版本号为2.1.0
2. 如需使用PostgreSQL，添加`yixsoft-batis-dialect-pg`依赖
3. 其他配置无需变更，完全兼容

### 从1.x升级到2.0.0

1. 升级Java版本到17+
2. 升级Spring Boot到3.2+
3. 更新依赖版本号为2.0.0
4. 检查不兼容的API变更（如有）