package com.cvs.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器（分页插件在 3.5.9+ 需额外引入 jsqlparser）
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }
}
