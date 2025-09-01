package com.ckrey.ckreycodemother;

import com.ckrey.ckreycodemother.ai.tools.BaseTool;
import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.ckrey.ckreycodemother.mapper")
@EnableCaching
public class CkreyCodeMotherApplication {

    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext context = SpringApplication.run(CkreyCodeMotherApplication.class, args);

//        }

    }

}
