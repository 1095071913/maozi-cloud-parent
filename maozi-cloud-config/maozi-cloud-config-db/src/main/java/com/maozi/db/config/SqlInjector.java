package com.maozi.db.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SQL 注入器配置
 * <p>
 * 扩展 MyBatis-Plus 的默认 SQL 注入器，在原有方法基础上
 * 新增批量插入方法 {@link InsertBatchSomeColumn}，
 * 排除填充类型为 UPDATE 的字段（即仅插入非更新填充的字段）。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class SqlInjector extends DefaultSqlInjector {

    /**
     * 获取注入的方法列表
     * <p>
     * 在默认方法列表基础上添加批量插入方法。
     * </p>
     *
     * @param configuration MyBatis 配置
     * @param mapperClass Mapper 接口类型
     * @param tableInfo 表信息
     * @return 方法列表
     */
    @Override
    public List<AbstractMethod> getMethodList(org.apache.ibatis.session.Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {

        List<AbstractMethod> methodList = super.getMethodList(configuration,mapperClass, tableInfo);

        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));

        return methodList;

    }

}
