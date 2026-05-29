package com.maozi.base.plugin.impl.join;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.JoinBasePlugin;
import com.maozi.base.plugin.join.JoinPlugin;
import com.maozi.common.ObjectUtil;
import org.apache.commons.lang3.StringUtils;

public class QueryLeftJoinPlugin extends JoinBasePlugin {

    @Override
    public void apply(String abbreviationModelName,MPJLambdaWrapper<?> wrapper, JoinPlugin joinPlugin) {

        ObjectUtil.isNullEmptyThrowError(joinPlugin.tableName(),abbreviationModelName + "内联目标");

        ObjectUtil.isNullEmptyThrowError(joinPlugin.on(),abbreviationModelName + "内联条件");

        String tableName = joinPlugin.tableName();

        if(StringUtils.isNotBlank(joinPlugin.tableAlias())){
            tableName += " as " + joinPlugin.tableAlias();
        }

        wrapper.leftJoin(tableName + " on " + joinPlugin.on());

    }
    
}
