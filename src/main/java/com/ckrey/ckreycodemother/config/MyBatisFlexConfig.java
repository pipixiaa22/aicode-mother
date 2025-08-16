package com.ckrey.ckreycodemother.config;

import com.mybatisflex.core.FlexGlobalConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 配置类（通常在 Spring Boot 项目的 @Configuration 类中）
@Configuration
public class MyBatisFlexConfig {
    @Bean
    public FlexGlobalConfig configurationCustomizer() {
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();


        defaultConfig.setNormalValueOfLogicDelete(false);
        defaultConfig.setDeletedValueOfLogicDelete(true);
        return defaultConfig;
    }

}