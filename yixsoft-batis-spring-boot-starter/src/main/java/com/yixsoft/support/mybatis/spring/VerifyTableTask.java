package com.yixsoft.support.mybatis.spring;

import com.yixsoft.support.mybatis.autosql.configuration.sqlsource.AutoSqlSource;
import com.yixsoft.support.mybatis.autosql.configuration.support.TableStructureUtils;
import com.yixsoft.support.mybatis.autosql.configuration.support.config.SqlGenerationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;

public class VerifyTableTask implements ApplicationListener<ApplicationStartedEvent> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final YixMyBatisConfig config;

    public VerifyTableTask(YixMyBatisConfig config) {
        this.config = config;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (config.isEnableStrictTableCheck()) {
            logger.info("starting check tables");
            Map<String, SqlGenerationConfig> tables = AutoSqlSource.getAllAutoSqlTables();
            for (Map.Entry<String, SqlGenerationConfig> tableEntry : tables.entrySet()) {
                TableStructureUtils.getTableColumns(tableEntry.getValue().getDialect(), tableEntry.getKey());
                logger.debug("loaded table {}", tableEntry.getKey());
            }
        } else {
            logger.info("strict table check disabled, skip");
        }
    }
}
