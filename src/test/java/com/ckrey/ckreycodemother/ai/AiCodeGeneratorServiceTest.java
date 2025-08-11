package com.ckrey.ckreycodemother.ai;

import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiChat;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult htmlCodeResult = aiChat.generateHtmlCode("帮我做一个简单的博客，不超过20行");
        Assertions.assertNotNull(htmlCodeResult);
    }

    @Test
    void generateMultiFileCode() {
    }
}