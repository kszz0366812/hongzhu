package com.insightflow.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Properties;

@Configuration
public class MyBatisPlusConfig implements MetaObjectHandler {

    /**
     * 配置PageHelper分页插件
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        // 设置数据库类型
        properties.setProperty("helperDialect", "mysql");
        // 分页参数合理化
        properties.setProperty("reasonable", "true");
        // 支持通过Mapper接口参数来传递分页参数
        properties.setProperty("supportMethodsArguments", "true");
        // 分页插件会自动检测当前的数据库链接，自动选择合适的分页方式
        properties.setProperty("autoRuntimeDialect", "true");
        pageHelper.setProperties(properties);
        return pageHelper;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}