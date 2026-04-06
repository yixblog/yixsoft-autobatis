package com.yixsoft.support.mybatis.test.pg;

import com.yixsoft.support.mybatis.autosql.dialects.ISqlDialect;
import com.yixsoft.support.mybatis.autosql.dialects.SqlDialectManager;
import com.yixsoft.support.mybatis.autosql.dialects.SupportsDatabase;
import com.yixsoft.support.mybatis.autosql.dialects.entities.RowNumberConfig;
import com.yixsoft.support.mybatis.dialects.pg.PostgreSqlDialect;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostgreSQL方言集成测试
 * 使用本地PostgreSQL数据库进行测试
 */
@SpringBootTest
@ActiveProfiles("pg-local")
@DisplayName("PostgreSQL方言集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostgreSqlDialectIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    static void checkDatabaseConnection(@Autowired DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertTrue(conn.isValid(5), "数据库连接应该有效");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // 每个测试前创建测试表
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 删除已存在的测试表
            stmt.execute("DROP TABLE IF EXISTS test_users CASCADE");
            stmt.execute("DROP TABLE IF EXISTS \"order\" CASCADE");
            
            // 创建测试用户表
            stmt.execute("""
                CREATE TABLE test_users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(100) NOT NULL,
                    email VARCHAR(200),
                    is_active BOOLEAN DEFAULT true,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    score NUMERIC(10,2),
                    metadata JSONB
                )
            """);
            
            // 插入测试数据
            for (int i = 1; i <= 5; i++) {
                stmt.execute(String.format(
                    "INSERT INTO test_users (username, email, score, is_active) VALUES ('user%d', 'user%d@test.com', %.2f, %s)",
                    i, i, i * 10.5, i % 2 == 0 ? "true" : "false"
                ));
            }
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        // 清理测试表
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS test_users CASCADE");
            stmt.execute("DROP TABLE IF EXISTS \"order\" CASCADE");
        }
    }

    // ==================== 关键字转义测试 ====================

    @Test
    @Order(1)
    @DisplayName("测试关键字转义在SQL中的应用")
    void testKeywordEscaping_inSql() throws Exception {
        // 创建包含保留关键字的表
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("""
                CREATE TABLE "order" (
                    id BIGSERIAL PRIMARY KEY,
                    "user_id" BIGINT,
                    amount NUMERIC(10,2)
                )
            """);
            
            // 插入数据测试
            stmt.execute("INSERT INTO \"order\" (\"user_id\", amount) VALUES (1, 100.50)");
            
            // 查询测试
            var rs = stmt.executeQuery("SELECT * FROM \"order\"");
            assertTrue(rs.next(), "应该能查询到数据");
            assertEquals(1L, rs.getLong("user_id"));
            assertEquals(100.50, rs.getBigDecimal("amount").doubleValue(), 0.01);
        }
    }

    @Test
    @Order(2)
    @DisplayName("测试escapeKeyword方法")
    void testEscapeKeyword() {
        PostgreSqlDialect dialect = new PostgreSqlDialect();
        
        assertEquals("\"order\"", dialect.escapeKeyword("order"));
        assertEquals("\"user_id\"", dialect.escapeKeyword("user_id"));
        assertEquals("\"select\"", dialect.escapeKeyword("select"));
        assertEquals("\"username\"", dialect.escapeKeyword("username"));
    }

    // ==================== ROW_NUMBER测试 ====================

    @Test
    @Order(3)
    @DisplayName("测试ROW_NUMBER函数生成")
    void testRowNumberFragment() {
        PostgreSqlDialect dialect = new PostgreSqlDialect();
        
        RowNumberConfig config = new RowNumberConfig("rn", "ORDER BY score DESC");
        String fragment = dialect.rowNumberFragment(config);
        
        assertEquals("row_number() over (ORDER BY score DESC) as rn", fragment);
        assertTrue(fragment.contains("row_number()"));
        assertTrue(fragment.contains("ORDER BY score DESC"));
        assertTrue(fragment.contains("as rn"));
    }

    @Test
    @Order(4)
    @DisplayName("测试ROW_NUMBER在查询中的应用")
    void testRowNumberInQuery() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 使用ROW_NUMBER进行排名查询
            var rs = stmt.executeQuery("""
                SELECT id, username, score,
                       row_number() over (ORDER BY score DESC) as rank
                FROM test_users
                ORDER BY rank
            """);
            
            int count = 0;
            double prevScore = Double.MAX_VALUE;
            while (rs.next()) {
                count++;
                double score = rs.getDouble("score");
                assertTrue(score <= prevScore, "分数应该按降序排列");
                prevScore = score;
            }
            assertEquals(5, count, "应该有5条记录");
        }
    }

    // ==================== 数据类型测试 ====================

    @Test
    @Order(5)
    @DisplayName("测试数据类型 - BIGSERIAL")
    void testDataType_bigserial() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            var rs = stmt.executeQuery("""
                SELECT data_type 
                FROM information_schema.columns 
                WHERE table_name = 'test_users' AND column_name = 'id'
            """);
            
            assertTrue(rs.next(), "应该找到id列");
            String dataType = rs.getString("data_type");
            // BIGSERIAL在information_schema中显示为bigint
            assertTrue("bigint".equals(dataType) || "bigserial".equals(dataType),
                    "id列应该是bigint或bigserial类型");
        }
    }

    @Test
    @Order(6)
    @DisplayName("测试数据类型 - JSONB")
    void testDataType_jsonb() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            var rs = stmt.executeQuery("""
                SELECT data_type 
                FROM information_schema.columns 
                WHERE table_name = 'test_users' AND column_name = 'metadata'
            """);
            
            assertTrue(rs.next(), "应该找到metadata列");
            assertEquals("jsonb", rs.getString("data_type"), "metadata列应该是jsonb类型");
        }
    }

    @Test
    @Order(7)
    @DisplayName("测试JSONB数据操作")
    void testJsonbOperation() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 插入JSONB数据
            stmt.execute("UPDATE test_users SET metadata = '{\"role\": \"admin\", \"settings\": {\"theme\": \"dark\"}}'::jsonb WHERE id = 1");
            
            // 查询JSONB数据
            var rs = stmt.executeQuery("SELECT metadata->>'role' as role FROM test_users WHERE id = 1");
            assertTrue(rs.next());
            assertEquals("admin", rs.getString("role"));
        }
    }

    // ==================== 表结构查询测试 ====================

    @Test
    @Order(8)
    @DisplayName("测试information_schema查询")
    void testInformationSchemaQuery() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 查询test_users表的所有列
            var rs = stmt.executeQuery("""
                SELECT column_name, data_type, is_nullable
                FROM information_schema.columns
                WHERE table_name = 'test_users'
                ORDER BY ordinal_position
            """);
            
            int columnCount = 0;
            while (rs.next()) {
                columnCount++;
                String columnName = rs.getString("column_name");
                String dataType = rs.getString("data_type");
                String isNullable = rs.getString("is_nullable");
                
                assertNotNull(columnName, "列名不应为null");
                assertNotNull(dataType, "数据类型不应为null");
                assertNotNull(isNullable, "是否可空不应为null");
            }
            
            assertTrue(columnCount >= 7, "test_users表应该至少有7列");
        }
    }

    // ==================== 分页测试 ====================

    @Test
    @Order(9)
    @DisplayName("测试LIMIT OFFSET分页")
    void testPagination() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 第一页
            var rs1 = stmt.executeQuery("SELECT * FROM test_users ORDER BY id LIMIT 2 OFFSET 0");
            int count1 = 0;
            while (rs1.next()) count1++;
            assertEquals(2, count1, "第一页应该有2条记录");
            
            // 第二页
            var rs2 = stmt.executeQuery("SELECT * FROM test_users ORDER BY id LIMIT 2 OFFSET 2");
            int count2 = 0;
            while (rs2.next()) count2++;
            assertEquals(2, count2, "第二页应该有2条记录");
        }
    }

    // ==================== Boolean类型测试 ====================

    @Test
    @Order(10)
    @DisplayName("测试Boolean类型处理")
    void testBooleanType() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 查询is_active为true的记录
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM test_users WHERE is_active = true");
            assertTrue(rs.next());
            int activeCount = rs.getInt(1);
            assertTrue(activeCount > 0, "应该有active用户");
        }
    }

    // ==================== NUMERIC类型测试 ====================

    @Test
    @Order(11)
    @DisplayName("测试NUMERIC类型处理")
    void testNumericType() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            var rs = stmt.executeQuery("SELECT SUM(score) as total FROM test_users");
            assertTrue(rs.next());
            double total = rs.getDouble("total");
            assertTrue(total > 0, "总分应该大于0");
        }
    }

    // ==================== @SupportsDatabase注解测试 ====================

    @Test
    @Order(12)
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
}