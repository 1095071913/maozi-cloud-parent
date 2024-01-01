package com.maozi.base.plugin.impl.join;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.JoinBasePlugin;
import com.maozi.base.plugin.join.JoinPlugin;
import com.maozi.common.BaseCommon;

public class QueryInnerJoinPlugin<T> extends JoinBasePlugin<T> {

    @Override
    public void apply(String abbreviationModelName,MPJLambdaWrapper<T> wrapper, JoinPlugin joinPlugin) {

        BaseCommon.isEmptyThrowError(joinPlugin.tableName(),abbreviationModelName+"内联目标");

        BaseCommon.isEmptyThrowError(joinPlugin.on(),abbreviationModelName+"内联条件");

        String tableName = joinPlugin.tableName();

        if(BaseCommon.isNotEmpty(joinPlugin.tableAlias())){
            tableName+=" as "+joinPlugin.tableAlias();
        }

        wrapper.innerJoin(tableName+" on "+joinPlugin.on());

    }

}
