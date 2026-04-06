# 实现任务清单

## 1. 项目版本升级

- [x] 1.1 更新父pom.xml中的revision属性从2.0.5改为2.1.0
- [x] 1.2 在父pom.xml的modules列表中添加yixsoft-batis-dialect-pg模块

## 2. 创建PostgreSQL方言模块

- [x] 2.1 创建yixsoft-batis-dialect-pg模块目录结构
- [x] 2.2 创建模块pom.xml文件，配置parent、artifactId和依赖关系
- [x] 2.3 在模块pom.xml中添加yixsoft-batis-dialects-core依赖（version=${revision})
- [x] 2.4 在模块pom.xml中添加PostgreSQL JDBC驱动依赖（scope=provided）

## 3. 实现PostgreSQL方言核心类

- [x] 3.1 创建PostgreSqlDialect.java主类，标注@SupportsDatabase({"postgresql", "postgres"})注解
- [x] 3.2 实现ISqlDialect接口的init方法，初始化PostgreSqlTableMapper
- [x] 3.3 实现ISqlDialect接口的selectTableColumns方法，查询并转换列信息
- [x] 3.4 实现ISqlDialect接口的escapeKeyword方法，使用双引号转义
- [x] 3.5 实现ISqlDialect接口的rowNumberFragment方法，返回标准row_number() over()语法

## 4. 实现表结构查询Mapper和DAO

- [x] 4.1 创建PostgreSqlTableMapper.java接口，定义selectColumns方法
- [x] 4.2 使用@Select注解编写查询information_schema.columns的SQL
- [x] 4.3 创建PostgreSqlColumnDAO.java实体类，定义列元数据字段
- [x] 4.4 实现数据类型映射逻辑，将PostgreSQL类型转换为JDBCType

## 5. 方言系统集成

- [x] 5.1 验证SqlDialectManager能正确扫描和注册PostgreSqlDialect
- [ ] 5.2 测试方言自动识别功能（通过@SupportsDatabase注解）
- [ ] 5.3 验证方言缓存机制，避免重复初始化
- [ ] 5.4 测试mybatis-paginator的PostgreSQLDialect集成配置

## 6. 单元测试

- [x] 6.1 创建PostgreSqlDialectTest单元测试类
- [x] 6.2 测试escapeKeyword方法的各种场景（关键字、普通名称、特殊字符）
- [x] 6.3 测试rowNumberFragment方法生成的SQL片段正确性
- [ ] 6.4 测试selectTableColumns方法的列信息转换逻辑
- [ ] 6.5 测试PostgreSQL特有数据类型映射（JSON、JSONB、UUID等）

## 7. 集成测试

- [x] 7.1 创建PostgreSQL集成测试环境（使用Testcontainers或真实数据库）
- [x] 7.2 测试完整的AutoMapper SQL生成流程（SELECT、INSERT、UPDATE、DELETE）
- [x] 7.3 测试分页查询功能（mybatis-paginator集成）
- [x] 7.4 测试复杂查询条件（WHERE、IN、IS NULL等）
- [x] 7.5 测试多方言环境下的方言切换和选择逻辑

## 8. 文档更新

- [x] 8.1 更新README.md，添加PostgreSQL方言使用说明
- [x] 8.2 更新docs/index.md，添加PostgreSQL数据库支持列表
- [x] 8.3 创建PostgreSQL方言配置指南文档
- [x] 8.4 创建PostgreSQL数据类型映射说明文档
- [x] 8.5 更新CHANGELOG.md，记录2.1.0版本新增PostgreSQL方言支持

## 9. 构建和发布

- [x] 9.1 执行完整的Maven构建（mvn clean install）
- [x] 9.2 验证所有模块构建成功，无编译错误和测试失败
- [x] 9.3 执行flatten-maven-plugin生成effective POM
- [ ] 9.4 签名发布 artifacts（GPG签名）
- [ ] 9.5 发布到Maven Central（ossrh profile）
- [ ] 9.6 发布到项目私有仓库（default profile）

## 10. 回归测试

- [x] 10.1 验证MySQL方言功能不受影响
- [x] 10.2 验证Oracle方言功能不受影响
- [x] 10.3 验证Spring Boot Starter正常工作
- [x] 10.4 验证现有测试套件全部通过
- [x] 10.5 验证多模块项目集成测试通过