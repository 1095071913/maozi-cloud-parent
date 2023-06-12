package com.maozi.mybatisplus.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
 
@Configuration
public class EasySqlInjector extends DefaultSqlInjector {
 
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
    	
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        
        return methodList;
        
    }
 
}