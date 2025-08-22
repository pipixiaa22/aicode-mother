package com.ckrey.ckreycodemother.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeRouterServiceFactory {


    @Resource
    private ChatModel chatModel;



    @Bean
    public AiCodeRouterService getAiCodeRouterService(){
        return AiServices.builder(AiCodeRouterService.class)
                .chatModel(chatModel)
                .build();
    }
}
