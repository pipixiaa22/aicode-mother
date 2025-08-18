package com.ckrey.ckreycodemother.core;

import com.ckrey.ckreycodemother.core.AiCodeGeneratorFacade;
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
        Flux<String> list = aiCodeGeneratorFacade.codeGenerateAndSaveStream("简单的任务记录网站，代码量不超过200行", CodeGenTypeEnum.VUE_PROJECT, 1L);
        List<String> result = list.collectList().block();
        String join = String.join("", result);
        System.out.println(join);
    }
}