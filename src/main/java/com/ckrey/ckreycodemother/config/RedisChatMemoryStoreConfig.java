package com.ckrey.ckreycodemother.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisChatMemoryStoreConfig {

    private String host;

    private Integer port;

    private String password;
    
    private String username;

    private long ttl;



    @Bean
    public RedisChatMemoryStore redisChatMemoryStore(){
        return RedisChatMemoryStore.builder().ttl(ttl)
                .host(host)
                .port(port)
                .build();
    }

}