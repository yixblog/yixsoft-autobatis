-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        10.3.11-MariaDB - mariadb.org binary distribution
-- 服务器OS:                        Win64
-- HeidiSQL 版本:                  10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table batis_test.sys_log
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE IF NOT EXISTS `sys_log`
(
    `id`      varchar(50) NOT NULL,
    `content` varchar(50) NOT NULL,
    `userid`  int(10) unsigned DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- Dumping data for table batis_test.sys_log: ~3 rows (大约)
/*!40000 ALTER TABLE `sys_log`
    DISABLE KEYS */;
INSERT INTO `sys_log` (`id`, `content`, `userid`)
VALUES ('aba', '11223', 1),
       ('cca', '22334', 1);
/*!40000 ALTER TABLE `sys_log`
    ENABLE KEYS */;

-- Dumping structure for table batis_test.sys_users
DROP TABLE IF EXISTS `sys_users`;
CREATE TABLE IF NOT EXISTS `sys_users`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name`        varchar(50)      NOT NULL,
    `description` text DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8;

-- Dumping data for table batis_test.sys_users: ~0 rows (大约)
/*!40000 ALTER TABLE `sys_users`
    DISABLE KEYS */;
INSERT INTO `sys_users` (`id`, `name`, `description`)
VALUES (1, 'user1', 'olllllll');
/*!40000 ALTER TABLE `sys_users`
    ENABLE KEYS */;

-- Dumping structure for table batis_test.test_log2
DROP TABLE IF EXISTS `test_log2`;
CREATE TABLE IF NOT EXISTS `test_log2`
(
    `pkid` int(11)     NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL DEFAULT '0',
    PRIMARY KEY (`pkid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8;

-- Dumping data for table batis_test.test_log2: ~9 rows (大约)
/*!40000 ALTER TABLE `test_log2`
    DISABLE KEYS */;
INSERT INTO `test_log2` (`pkid`, `name`)
VALUES (1, 'hhh'),
       (2, 'hhh'),
       (3, 'hhh'),
       (4, 'hhh'),
       (5, 'hhh'),
       (6, 'hhh'),
       (7, 'hhh'),
       (8, 'hhh'),
       (9, 'hhh');
/*!40000 ALTER TABLE `test_log2`
    ENABLE KEYS */;

-- Dumping structure for table batis_test.type_table
DROP TABLE IF EXISTS `type_table`;
CREATE TABLE IF NOT EXISTS `type_table`
(
    `id`    int(10) unsigned NOT NULL AUTO_INCREMENT,
    `col1`  tinyint(4)            DEFAULT NULL,
    `col2`  smallint(6)           DEFAULT NULL,
    `col3`  mediumint(9)          DEFAULT NULL,
    `col4`  bigint(20)            DEFAULT NULL,
    `col5`  bit(1)                DEFAULT NULL,
    `col6`  float                 DEFAULT NULL,
    `col7`  double                DEFAULT NULL,
    `col8`  decimal(10, 0)        DEFAULT NULL,
    `col9`  char(10)              DEFAULT NULL,
    `col10` varchar(50)           DEFAULT NULL,
    `col11` tinytext              DEFAULT NULL,
    `col12` mediumtext            DEFAULT NULL,
    `col13` longtext              DEFAULT NULL,
    `col14` binary(50)            DEFAULT NULL,
    `col15` varbinary(50)         DEFAULT NULL,
    `col16` tinyblob              DEFAULT NULL,
    `col17` blob                  DEFAULT NULL,
    `col18` mediumblob            DEFAULT NULL,
    `col19` longblob              DEFAULT NULL,
    `col20` date                  DEFAULT NULL,
    `col21` time                  DEFAULT NULL,
    `col22` year(4)               DEFAULT NULL,
    `col23` datetime              DEFAULT NULL,
    `col24` timestamp        NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

-- Dumping data for table batis_test.type_table: ~2 rows (大约)
/*!40000 ALTER TABLE `type_table`
    DISABLE KEYS */;
INSERT INTO `type_table` (`id`, `col1`, `col2`, `col3`, `col4`, `col5`, `col6`, `col7`, `col8`, `col9`, `col10`,
                          `col11`, `col12`, `col13`, `col14`, `col15`, `col16`, `col17`, `col18`, `col19`, `col20`,
                          `col21`, `col22`, `col23`, `col24`)
VALUES (1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL),
       (2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL);
/*!40000 ALTER TABLE `type_table`
    ENABLE KEYS */;

drop table if exists example;
create table example(
                        entity_id varchar(50) not null primary key ,
                        group_name varchar(20) not null,
                        create_time timestamp default current_timestamp,
                        remark varchar(20) null,
                        is_valid tinyint(1) default 1
)engine=InnoDB;
/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
