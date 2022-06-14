package com.yixsoft.support.mybatis.autosql.annotations;

/**
 * Ignore null param rule:
 *
 * - PARAM_TYPE_DETECT: default choice. If request object is a Map instance. will not ignore null. otherwise if request object is pojo object. will ignore null.
 * - ALWAYS: all null value will be ignored.
 * - NEVER: all null value will not be ignored.
 */
public enum IgnoreNullRule {
    PARAM_TYPE_DETECT,
    ALWAYS,
    NEVER
}
