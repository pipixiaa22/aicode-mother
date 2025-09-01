package com.ckrey.ckreycodemother.ai;

import com.ckrey.ckreycodemother.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AiCodeRouterServiceFactory {

    //即便routingChatModelPrototype为多例模式，但是，由于本身为单例注入。只进行了一次依赖注入


    public AiCodeRouterService createAiCodeRouterService(){
        //通过访问applicationcontext访问多个实例
        ChatModel routingChatModelPrototype = SpringContextUtil.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(AiCodeRouterService.class)
                .chatModel(routingChatModelPrototype)
                .build();
    }


    @Bean
    public AiCodeRouterService getAiCodeRouterService(){
        return createAiCodeRouterService();
    }
}
