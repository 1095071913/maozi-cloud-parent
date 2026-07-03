package com.maozi.opentelemetry.sampler.handle.filter.impl;

import com.maozi.opentelemetry.sampler.handle.filter.FilterHandle;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class MysqlFilterHandle extends FilterHandle {

    private List<String> names = List.of("mysql");

    private final List<String> EXCLUDED_STATEMENT = List.of(

            "SELECT ?",
            "SELECT version()",
            "SELECT DATABASE()",
            "SELECT GET_LOCK(?,?)",
            "SELECT RELEASE_LOCK(?)",

            "SELECT @@sql_safe_updates",
            "SELECT @@foreign_key_checks",
            "SELECT @@GLOBAL.ENFORCE_GTID_CONSISTENCY",

            "SELECT SUBSTRING_INDEX(USER(),?,?)",
            "SET foreign_key_checks=?, sql_safe_updates=?",
            "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?",
            "SELECT COUNT(?) FROM information_schema.schemata WHERE schema_name=? LIMIT ?",
            "select VARIABLE_VALUE from performance_schema.global_variables where variable_name = ?",
            "SELECT variable_name FROM performance_schema.user_variables_by_thread WHERE variable_value IS NOT NULL"

    );

    @Override
    public Boolean filter(Attributes attributes) {

        String type = attributes.get(InternalAttributeKeyImpl.create("db.system", AttributeType.STRING));
        if(Objects.isNull(type) || !getNames().contains(type)){
            return false;
        }

        String statement = attributes.get(InternalAttributeKeyImpl.create("db.statement", AttributeType.STRING));
        return EXCLUDED_STATEMENT.contains(statement);

    }

}
