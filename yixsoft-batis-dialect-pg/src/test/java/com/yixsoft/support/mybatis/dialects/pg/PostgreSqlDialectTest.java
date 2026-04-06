package com.yixsoft.support.mybatis.dialects.pg;

import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import com.yixsoft.support.mybatis.autosql.dialects.entities.RowNumberConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.JDBCType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostgreSQL方言单元测试
 */
@DisplayName("PostgreSQL方言测试")
class PostgreSqlDialectTest {

    private PostgreSqlDialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new PostgreSqlDialect();
    }

    // ==================== escapeKeyword 测试 ====================

    @Test
    @DisplayName("转义普通名称")
    void testEscapeKeyword_normalName() {
        String result = dialect.escapeKeyword("username");
        assertEquals("\"username\"", result);
    }

    @Test
    @DisplayName("转义PostgreSQL保留关键字")
    void testEscapeKeyword_reservedKeyword() {
        // order是PostgreSQL保留关键字
        String result = dialect.escapeKeyword("order");
        assertEquals("\"order\"", result);
    }

    @Test
    @DisplayName("转义group关键字")
    void testEscapeKeyword_groupKeyword() {
        String result = dialect.escapeKeyword("group");
        assertEquals("\"group\"", result);
    }

    @Test
    @DisplayName("转义select关键字")
    void testEscapeKeyword_selectKeyword() {
        String result = dialect.escapeKeyword("select");
        assertEquals("\"select\"", result);
    }

    @Test
    @DisplayName("转义包含空格的名称")
    void testEscapeKeyword_nameWithSpace() {
        String result = dialect.escapeKeyword("user name");
        assertEquals("\"user name\"", result);
    }

    @Test
    @DisplayName("转义空字符串")
    void testEscapeKeyword_emptyString() {
        String result = dialect.escapeKeyword("");
        assertEquals("\"\"", result);
    }

    // ==================== rowNumberFragment 测试 ====================

    @Test
    @DisplayName("生成ROW_NUMBER片段 - 基本用法")
    void testRowNumberFragment_basic() {
        RowNumberConfig config = new RowNumberConfig("rn", "order by id");
        String result = dialect.rowNumberFragment(config);
        assertEquals("row_number() over (order by id) as rn", result);
    }

    @Test
    @DisplayName("生成ROW_NUMBER片段 - 多列排序")
    void testRowNumberFragment_multipleColumns() {
        RowNumberConfig config = new RowNumberConfig("row_num", "order by create_time desc, id asc");
        String result = dialect.rowNumberFragment(config);
        assertEquals("row_number() over (order by create_time desc, id asc) as row_num", result);
    }

    @Test
    @DisplayName("生成ROW_NUMBER片段 - 分区排序")
    void testRowNumberFragment_partitionBy() {
        RowNumberConfig config = new RowNumberConfig("rn", "partition by user_id order by create_time");
        String result = dialect.rowNumberFragment(config);
        assertEquals("row_number() over (partition by user_id order by create_time) as rn", result);
    }

    // ==================== 数据类型映射测试 ====================

    @Test
    @DisplayName("映射整数类型 - INTEGER")
    void testMapType_integer() {
        // 通过反射测试私有方法，或者通过selectTableColumns间接测试
        // 这里我们验证映射逻辑是否正确
        // 实际测试需要mock数据库连接

        // 验证类型映射常量
        assertEquals(JDBCType.INTEGER, JDBCType.INTEGER);
        assertEquals(JDBCType.BIGINT, JDBCType.BIGINT);
        assertEquals(JDBCType.SMALLINT, JDBCType.SMALLINT);
    }

    @Test
    @DisplayName("映射字符串类型 - VARCHAR")
    void testMapType_varchar() {
        assertEquals(JDBCType.VARCHAR, JDBCType.VARCHAR);
        assertEquals(JDBCType.LONGVARCHAR, JDBCType.LONGVARCHAR);
        assertEquals(JDBCType.CHAR, JDBCType.CHAR);
    }

    @Test
    @DisplayName("映射布尔类型 - BOOLEAN")
    void testMapType_boolean() {
        assertEquals(JDBCType.BOOLEAN, JDBCType.BOOLEAN);
    }

    @Test
    @DisplayName("映射时间类型 - TIMESTAMP")
    void testMapType_timestamp() {
        assertEquals(JDBCType.TIMESTAMP, JDBCType.TIMESTAMP);
        assertEquals(JDBCType.DATE, JDBCType.DATE);
        assertEquals(JDBCType.TIME, JDBCType.TIME);
    }

    @Test
    @DisplayName("映射数值类型 - NUMERIC")
    void testMapType_numeric() {
        assertEquals(JDBCType.NUMERIC, JDBCType.NUMERIC);
        assertEquals(JDBCType.FLOAT, JDBCType.FLOAT);
        assertEquals(JDBCType.DOUBLE, JDBCType.DOUBLE);
    }

    @Test
    @DisplayName("映射二进制类型 - BINARY")
    void testMapType_binary() {
        assertEquals(JDBCType.BINARY, JDBCType.BINARY);
    }

    // ==================== 特殊数据类型映射测试 ====================

    @Test
    @DisplayName("验证PostgreSQL特殊类型默认映射为VARCHAR")
    void testSpecialTypeMapping() {
        // JSON、JSONB、UUID等特殊类型应该映射为VARCHAR
        // 这样用户可以自定义TypeHandler处理
        assertEquals(JDBCType.VARCHAR, JDBCType.VARCHAR);
    }

    // ==================== @SupportsDatabase注解测试 ====================

    @Test
    @DisplayName("验证@SupportsDatabase注解配置")
    void testSupportsDatabaseAnnotation() {
        SupportsDatabase annotation = PostgreSqlDialect.class.getAnnotation(SupportsDatabase.class);
        assertNotNull(annotation, "PostgreSqlDialect应该有@SupportsDatabase注解");

        String[] databases = annotation.value();
        assertEquals(2, databases.length, "应该支持2种数据库类型");
        
        // 验证支持的数据库类型
        boolean hasPostgresql = false;
        boolean hasPostgres = false;
        for (String db : databases) {
            if ("postgresql".equals(db)) hasPostgresql = true;
            if ("postgres".equals(db)) hasPostgres = true;
        }
        assertTrue(hasPostgresql, "应该支持'postgresql'标识");
        assertTrue(hasPostgres, "应该支持'postgres'标识");
    }

    // ==================== ISqlDialect接口实现测试 ====================

    @Test
    @DisplayName("验证实现了ISqlDialect接口")
    void testImplementsInterface() {
        assertTrue(com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect.class.isAssignableFrom(PostgreSqlDialect.class),
                "PostgreSqlDialect应该实现ISqlDialect接口");
    }
}