# YixSoft-Autobatis 文档导航

本文档为 YixSoft-Autobatis 插件的完整文档导航索引，帮助您快速找到所需信息。

## 文档概览

YixSoft-Autobatis 是一个基于 MyBatis 的自动 SQL 生成插件，通过注解驱动的方式简化单表 CRUD 操作的开发工作。

### 核心特性

- **注解驱动**：通过 `@AutoMapper` 和 `@AutoSql` 注解声明表映射和 SQL 类型
- **自动表结构感知**：运行时自动获取数据库表元数据，确保 SQL 与表结构一致性
- **智能参数处理**：自动处理 null 值判断、集合展开为 IN 条件等场景
- **多数据库支持**：方言系统自动适配 MySQL、Oracle、PostgreSQL 等数据库
- **主键自动生成**：支持 UUID、SnowFlake、数据库自增等多种策略

## 文档导航

### 快速开始

- **[快速开始指南](quick-start.md)**：5分钟快速上手教程
  - Maven依赖配置
  - Spring Boot配置
  - 基础CRUD操作示例
  - 常见问题排查

### 核心概念

- **[注解详解](annotations.md)**：完整注解体系说明
  - `@AutoMapper`：表映射配置
  - `@AutoSql`：SQL类型声明
  - `@Column`：字段列名映射
  - `@EnumValue`：枚举值映射
  - `@StaticUpdate`：静态更新片段
  - `@AdvanceSelect`：高级查询配置
  - 分页相关注解

### 进阶指南

- **[最佳实践](best-practices.md)**：高效使用指南
  - 主键生成策略选择
  - null值处理策略
  - 参数类型选择
  - 性能优化建议
  - 异常处理
  - 枚举处理
  - 表名和列名映射
  - 分页优化
  - 代码组织
  - 测试实践

- **[高级用法](advanced-usage.md)**：高级功能详解
  - 动态条件查询
  - 多列主键操作
  - row_number排序查询
  - 固定过滤条件
  - 排除敏感字段
  - 静态更新操作
  - 高级分页优化
  - 条件查询组合
  - 复杂WHERE条件
  - 方言适配
  - 自定义扩展
  - 性能调优

### 架构与扩展

- **[架构设计](../openspec/specs/architecture.md)**：技术架构详解
  - 整体架构层次
  - 核心组件职责
  - 关键设计模式
  - 数据流分析
  - 扩展点设计
  - 性能考虑
  - 设计权衡

- **[扩展机制](../openspec/specs/extension-mechanisms.md)**：定制化指南
  - 方言扩展
  - 主键生成器扩展
  - SQL Provider扩展
  - InterfaceMapperConfigurator扩展
  - WhereCondition扩展
  - 全局配置扩展
  - Spring Boot自动配置扩展
  - 扩展最佳实践

### 项目信息

- **[项目概述](../openspec/specs/project-overview.md)**：整体介绍
  - 项目定位
  - 核心价值
  - 技术栈
  - 模块组成
  - 适用场景
  - 版本历史

- **[核心功能](../openspec/specs/core-features.md)**：功能详解
  - 注解驱动体系
  - SQL生成机制
  - 主键自动生成
  - 方言系统
  - 分页集成
  - 异常处理

- **[使用指南](../openspec/specs/usage-guide.md)**：完整使用示例
  - 快速开始
  - 常见使用场景
  - 最佳实践
  - 常见问题与解决方案

## 按场景查找

### 我是新手，想快速上手

1. 阅读 [快速开始指南](quick-start.md)
2. 了解核心注解 [注解详解](annotations.md)
3. 学习基础用法 [使用指南](../openspec/specs/usage-guide.md)

### 我遇到了问题

1. 查看 [快速开始指南](quick-start.md) 中的常见问题排查
2. 参考 [最佳实践](best-practices.md) 中的异常处理
3. 查阅 [使用指南](../openspec/specs/usage-guide.md) 中的常见问题与解决方案

### 我想要优化性能

1. 学习 [最佳实践](best-practices.md) 中的性能优化建议
2. 了解 [高级用法](advanced-usage.md) 中的静态更新、排除字段等技巧
3. 参考 [架构设计](../openspec/specs/architecture.md) 中的性能考虑

### 我需要扩展功能

1. 阅读 [扩展机制](../openspec/specs/extension-mechanisms.md)
2. 了解方言扩展、主键生成器扩展等定制化方案
3. 参考 [架构设计](../openspec/specs/architecture.md) 中的扩展点设计

### 我想深入了解架构

1. 阅读 [架构设计](../openspec/specs/architecture.md)
2. 了解核心组件职责和关键设计模式
3. 参考 [核心功能](../openspec/specs/core-features.md) 中的SQL生成机制

## 按主题查找

### 配置相关

- Maven依赖配置：[快速开始指南](quick-start.md)
- Spring Boot配置：[快速开始指南](quick-start.md)
- 全局配置扩展：[扩展机制](../openspec/specs/extension-mechanisms.md)

### 注解相关

- 核心注解：[注解详解](annotations.md)
- 注解使用示例：[使用指南](../openspec/specs/usage-guide.md)
- 注解组合使用：[注解详解](annotations.md)

### 参数相关

- 参数类型选择：[最佳实践](best-practices.md)
- 动态条件查询：[高级用法](advanced-usage.md)
- null值处理：[最佳实践](best-practices.md)

### 主键相关

- 主键生成策略：[注解详解](annotations.md)
- 主键生成器选择：[最佳实践](best-practices.md)
- 多列主键：[高级用法](advanced-usage.md)
- 自定义主键生成器：[扩展机制](../openspec/specs/extension-mechanisms.md)

### 查询相关

- 基础查询：[快速开始指南](quick-start.md)
- 动态条件查询：[高级用法](advanced-usage.md)
- 固定过滤条件：[高级用法](advanced-usage.md)
- 排除字段查询：[高级用法](advanced-usage.md)
- 分页查询：[最佳实践](best-practices.md)

### 更新相关

- 基础更新：[快速开始指南](quick-start.md)
- 静态更新：[注解详解](annotations.md) 和 [高级用法](advanced-usage.md)
- null值处理：[最佳实践](best-practices.md)

### 方言相关

- 方言配置：[快速开始指南](quick-start.md)
- 方言系统详解：[核心功能](../openspec/specs/core-features.md)
- 方言适配：[高级用法](advanced-usage.md)
- 方言扩展：[扩展机制](../openspec/specs/extension-mechanisms.md)

### 性能相关

- 性能优化建议：[最佳实践](best-practices.md)
- 高级分页优化：[高级用法](advanced-usage.md)
- 表结构缓存：[高级用法](advanced-usage.md)
- 性能调优：[高级用法](advanced-usage.md)

## 外部资源

- **源码仓库**：https://gitee.com/yixsoft/yixsoft-autobatis
- **Maven Central**：搜索 `com.yixsoft:yixsoft-batis-spring-boot-starter`
- **GitHub Packages**：发布到GitHub Packages
- **私有Nexus**：发布到私有Nexus仓库

## 版本信息

- **当前版本**：2.0.5
- **Java版本要求**：Java 17+
- **Spring Boot版本**：3.2.3+
- **MyBatis版本**：3.5.0+

## 开发团队

- **主要开发者**：Astro Dai (davepotter@163.com)
- **开源协议**：MIT License

## 文档反馈

如果您发现文档有误或需要补充，请通过以下方式反馈：

- 在源码仓库提交 Issue
- 发送邮件到 davepotter@163.com

## AI Coding 集成指南

本文档体系专门为 AI Coding 项目设计，便于 AI 助手快速学习和使用本项目：

### 文档结构设计

- **README.md**：项目主文档，包含核心介绍和快速示例
- **docs/quick-start.md**：5分钟快速上手教程
- **docs/annotations.md**：完整注解体系说明（带表格和示例）
- **docs/best-practices.md**：最佳实践指南（场景化推荐）
- **docs/advanced-usage.md**：高级功能详解（复杂场景）
- **docs/index.md**：文档导航索引（本文档）
- **openspec/specs/**：详细技术文档（架构、扩展、功能、使用指南）

### AI 助手使用建议

1. **快速理解**：从 README.md 和 docs/quick-start.md 开始
2. **深入学习**：阅读 docs/annotations.md 和 docs/best-practices.md
3. **解决问题**：根据 docs/index.md 中的"按场景查找"定位文档
4. **扩展开发**：阅读 openspec/specs/extension-mechanisms.md

### 文档特点

- **结构化清晰**：每份文档职责明确，便于快速定位
- **示例丰富**：大量代码示例和配置示例
- **表格总结**：关键信息以表格形式呈现，便于理解
- **场景驱动**：按使用场景组织内容，符合实际开发流程
- **中文编写**：所有文档使用中文，便于国内开发者理解

## 总结

本文档体系提供了从快速上手到深入扩展的完整指南，无论您是新手还是有经验的开发者，都能找到所需信息。建议按照以下路径学习：

1. **新手路径**：README.md → quick-start.md → annotations.md → best-practices.md
2. **进阶路径**：advanced-usage.md → architecture.md → extension-mechanisms.md
3. **问题解决**：根据 docs/index.md 中的"按场景查找"定位相关文档

祝您使用愉快！