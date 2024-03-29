# YixSoft MyBatis AutoSql Extension

This extension can auto generate and bind simple CRUD sql to your mapper methods.

Save your time to build simple sql.

On startup, this extension will try to connect to datasource to get database metadata so it can recognize which dialect should be used.
Of course, you should register the dialect class in the configuration.

Supports basic sql on single table. Including SELECT list, SELECT one, SELECT count, UPDATE by primary key, INSERT(supports auto-generated primary key), DELETE.
in WHERE clause, if a parameter was provided as a collection or array, it will auto generate a IN clause.

Any auto-generated sql will try to connect to database and get table structure. and structure will be cached in 10min.

As parameter was based on Map, keys will only be ignored when there is no key in the parameter names. If you give a null value,
it will be passed as a null value.

## Configurations
MAVEN
```xml
<dependency>
  <groupId>com.yixsoft</groupId>
  <artifactId>yixsoft-batis-spring-boot-starter</artifactId>
  <version>${autobatis.version}</version>
</dependency>
```
```xml
<!-- add dialect dependency -->
<dependency>
  <groupId>com.yixsoft</groupId>
  <artifactId>yixsoft-batis-dialect-mysql</artifactId>
  <version>${autobatis.version}</version>
</dependency>
```

```yaml
# first you have to configure datasource
mybatis:
  default-key-generator: com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider #default key generator if not determined in mapper
  enable-strict-table-check: false  # if enabled, system will scan all table annotated in AutoMapper to find if any table has not created
  configuration: # normal mybatis configurations
    mapUnderscoreToCamelCase: true
  dialects: # auto sql dialect config, here is an example, default was empty
    - com.yixsoft.support.mybatis.dialects.mysql.MySqlDialect
  paginator: # paginator config
    enable: true  # enable com.github.meimeidev:mybatis-paginator to help build pagelist
    advanced-count: true # we provide a @AdvancedPaginator annotation to determine the count select in case the detailed select cost too much time. and it requires com.github.meimeidev:mybatis-paginator 
    dialect: com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect # com.github.meimeidev:mybatis-paginator required configuration
    async-total-count: false # com.github.meimeidev:mybatis-paginator configuration
    pool-max-size: 0 # com.github.meimeidev:mybatis-paginator configuration, if it was <=0 will not be applied
  config-location: 'classpath:mybatis.xml' # can determine your mybatis configuration configuration file
```

## Usage

### @AutoMapper
Should be marked on all mapper interfaces which requires to register auto-sql. it determines the table name, primary key name/names.
Properties:
- tablename: determine the table name
- pkName: determine the primary key name/names(if you use multi-column primary key, we will not auto generate primary key)
- keyGenerator: primary key generator. if you configured single column primary key and leave the primary key empty, we will auto generate one for you.
Available key generators:
  - `com.yixsoft.support.mybatis.autosql.pk.UUIDPkProvider`: UUID string primary key. Which is the default choice if you leave this parameter empty. 
  - `org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator`: Auto Increment key from Database. Usually to be a Integer or Long value.
  - `com.yixsoft.support.mybatis.autosql.pk.SnowFlakeLongKeyProvider`: Snow Flake key generator. Which provides a unique Long value.
  - `com.yixsoft.support.mybatis.autosql.pk.SnowFlakeStringKeyProvider`: Snow Flake key generator. Which provides a unique String value.

### @AutoSql
Common auto sql annotation which should be marked on methods of mappers. and it determines which type is the AutoSql
Types:
- INSERT: auto generate an insert sql
- UPDATE: auto generate an update sql
- DELETE: auto generate a delete sql
- SELECT: auto generate a select sql
- COUNT: auto generate a select count sql

ignoreNullRule:
- PARAM_TYPE_DETECT: default if not set. If parameter is a Map, auto sql will not ignore null value. Otherwise will ignore if parameter is a Java Bean
- ALWAYS: always ignore null value.
- NEVER: never ignore null value

### @Column

Can be marked on getter method or related field name in Java Bean. to determine which column this field was related to.

Only available for parameters in AutoSql generation at the moment. Which means it will NOT work on traditional MyBatis mapper references or in MyBatis Deserialization

### @EnumValue

By default, Enum values will use Enum.name() as target value which saved in database. 

But you can use this enum to mark a field or getter method as the enum value. Which supports both serialize and deserialize.

For example, this code will mark the code value as the value stored in database.:

```java
public class MyEnum{
    GUEST(0),MEMBER(1),VIP(2);
    
    @EnumValue
    private final int code;
    MyEnum(int code){
        this.code = code;
    }
}
```

### Advanced

#### @StaticUpdate
Can replace `@AutoSql(UPDATE)`, and value is the static update sql fragment. 

for example, you want to execute `update foo set is_valid=0 where id=#{id}`, the value can input `is_valid=0`

so the mapping should be 
```java
@AutoMapper(tablename="foo", pkName="id")
public interface FooMapper{
    @StaticUpdate("is_valid=0")
    void markDisabled(@Param("id") String id);
}
```

#### @AdvanceSelect
Like `@StaticUpdate`, it can replace `@AutoSql(SELECT)`. This annotation provides 2 extended configurations:
- excludeColumns: If you do not want to select some columns such as blob/clob columns, secure columns in the auto generated select sql, you can use this config
- addonWhereClause: You can add some where fragments in the auto generated select sql. it will concat generated where clauses with `AND`

#### @AdvancedPaginator, @CountRef, @CountSqlTpl
These annotations are developed to upgrade com.github.meimeidev:mybatis-paginator. 

In this plugin, sql was simply wrapped by `select count(1) from ($originSql)` to get total counts. But if the origin sql
was very complex, the count sql will take a lot of time and not all join fragments are useful for counting. So with these annotations,
You can determine count sql to another mapping ref or just give the count sql to simplify pagination select.

## AutoSql Dialect
Dialect class should implements `com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect`
and be annotated by `com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase` to determine which database it supports
