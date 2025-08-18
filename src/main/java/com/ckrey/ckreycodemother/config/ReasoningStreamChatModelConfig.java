package com.ckrey.ckreycodemother.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
public class ReasoningStreamChatModelConfig {

    private String baseUrl;

    private String apiKey;


    /**
     * 推理流式 模型，用于前端工程化项目构建
     * @return StreamingChatModel
     */
    @Bean
    public StreamingChatModel reasoningStreamChatModel() {

        final String modelName = "deepseek-chat";

        final int maxToken = 8192;

//        final String modelName = "deepseek-reasoner";
//
//        final int maxToken = 12768;

        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxToken)
                .logRequests(true)
                .logResponses(true)
                .build();

    }


}
