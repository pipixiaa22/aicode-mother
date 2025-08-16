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
        HtmlCodeResult htmlCodeResult1 = aiChat.generateHtmlCode( "不要生成网站，告诉我你刚刚做了什么？");
        Assertions.assertNotNull(htmlCodeResult1);

        HtmlCodeResult htmlCodeResult2 = aiChat.generateHtmlCode("帮我做一个简单的博客，不超过20行");
        Assertions.assertNotNull(htmlCodeResult2);
        HtmlCodeResult htmlCodeResult3 = aiChat.generateHtmlCode( "不要生成网站，告诉我你刚刚做了什么？");
        Assertions.assertNotNull(htmlCodeResult3);


    }

    @Test
    void generateMultiFileCode() {
    }
}