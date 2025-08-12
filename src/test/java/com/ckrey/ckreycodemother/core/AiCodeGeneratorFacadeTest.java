package com.ckrey.ckreycodemother.core;

import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiCodeGeneratorFacadeTest {




    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;








    @Test
    void codeGenerateAndSave() {
        Flux<String> test = aiCodeGeneratorFacade.codeGenerateAndSaveStream("帮我写一个简单的博客，不超过20行代码", CodeGenTypeEnum.MULTI_FILE);
        List<String> block = test.collectList().block();
        Assertions.assertNotNull(block);
    }
}