package com.ckrey.ckreycodemother;

import com.ckrey.ckreycodemother.ai.tools.BaseTool;
import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import spi.Ckrey;

import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.ckrey.ckreycodemother.mapper")
public class CkreyCodeMotherApplication {

    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext context = SpringApplication.run(CkreyCodeMotherApplication.class, args);
        for (BaseTool value : context.getBeansOfType(BaseTool.class).values()) {
            System.out.println(value.getClass().getName());
        }

    }

}
