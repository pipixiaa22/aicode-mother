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
        reactor.core.publisher.Flux<String> stringFlux = aiCodeGeneratorFacade.codeGenerateAndSave("帮我帮我做一个简单的博客，不超过20行", CodeGenTypeEnum.MULTI_FILE);

        List<String> block = stringFlux.collectList().block();

        Assertions.assertNotNull(block);

    }
}