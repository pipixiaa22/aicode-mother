package com.ckrey.ckreycodemother.ai;

import com.ckrey.ckreycodemother.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;

@Configuration
@Slf4j
public class AiCodeGenerateServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;

//    private HashMap<Long, AiCodeGeneratorService> hashMap = new HashMap<>();
//

    private final Cache<Long, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId:{},原因:{}", key, cause);
            }).build();


    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return serviceCache.get(appId, (key) -> createAiCodeGeneratorService(appId));
    }


    private AiCodeGeneratorService createAiCodeGeneratorService(long appId) {
        log.info("创建ai实例：{}", appId);

        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryStore(redisChatMemoryStore)
                .id(appId)
                .maxMessages(20)
                .build();

        //加载对话历史到记忆中，就是从mysql加载到redis中
        chatHistoryService.loadHistory(appId, chatMemory, 20);


        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();


    }


    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
//        return AiServices.builder(AiCodeGeneratorService.class)
//                .chatModel(chatModel)
//                .streamingChatModel(streamingChatModel)
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
//                        .chatMemoryStore(redisChatMemoryStore)
//                        .id(memoryId)
//                        .maxMessages(20)
//                        .build())
//                .build();
        return getAiCodeGeneratorService(0);
    }


}
