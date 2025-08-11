package com.ckrey.ckreycodemother.core;

import ch.qos.logback.classic.Logger;
import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.ai.AiCodeGeneratorService;
import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import com.ckrey.ckreycodemother.ai.model.MultiFileCodeResult;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;


    public Flux<String> codeGenerateAndSave(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {


        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提示词不能为空");
        }

        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }


        //这里可以直接用switch啊
        return switch (codeGenTypeEnum) {
            case HTML -> generateHtmlCodeAndSaveStream(userMessage);
            case MULTI_FILE -> generateMultiFileCodeAndSaveStream(userMessage);
            default -> {
                String errorMes = "暂不支持该种生成模式" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMes);
            }
        };


    }

    private Path generateHtmlCodeAndSave(String userMes) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMes);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    private Path generateMultiFileCodeAndSave(String userMes) {
        return CodeFileSaver.saveMultiFileCodeResult(aiCodeGeneratorService.generateMultiFileCode(userMes));
    }


    /**
     * 流式生成代码
     */

    private Flux<String> generateHtmlCodeAndSaveStream(String message){
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(message);
        StringBuilder stringBuilder = new StringBuilder();
        return result.doOnNext(stringBuilder::append).doOnComplete(()->{
           try {
               //这里要将完整的字符串解析后保存
               String str = stringBuilder.toString();
               //解析代码为对象
               HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(str);
               Path path = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
               log.info("文件保存成功，路径为{}", path);
           }
            catch (Exception e){
               log.error("保存失败,{}",e.getMessage());
            }
        });

    }

    private Flux<String> generateMultiFileCodeAndSaveStream(String message){
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(message);
        StringBuilder stringBuilder = new StringBuilder();
        return result.doOnNext(stringBuilder::append).doOnComplete(()->{
            //这里要将完整的字符串解析后保存
            String str = stringBuilder.toString();
            //解析代码为对象
            MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(str);
            Path path = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
            log.info("文件保存成功，路径为{}", path);
        });


    }

    public static void main(String[] args) {
        Path path = Paths.get("/home/ckrey/IdeaProjects/ckrey-code-mother/tmp/code_output/html_1953652459952889856/index.html");

        System.out.println(path);
    }


}
