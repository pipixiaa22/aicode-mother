package com.ckrey.ckreycodemother.core;

import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.ai.AiCodeGenerateServiceFactory;
import com.ckrey.ckreycodemother.ai.AiCodeGeneratorService;
import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import com.ckrey.ckreycodemother.ai.model.MultiFileCodeResult;
import com.ckrey.ckreycodemother.core.parser.CodeParserExecutor;
import com.ckrey.ckreycodemother.core.saver.CodeFileSaverExecutor;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Path;

@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGenerateServiceFactory aiCodeGenerateServiceFactory;


    /**
     * 流式输出代码，最后调用processCodeStream进行类的解析然后存入文件
     *
     * @param userMessage
     * @param codeGenTypeEnum
     * @param appId
     * @return
     */
    public Flux<String> codeGenerateAndSaveStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提示词不能为空");
        }

        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        //根据工厂类内的缓存获取ai服务实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGenerateServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);

        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            case VUE_PROJECT -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };

    }

    public File generateSaveCode(String userMes, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数枚举为空");
        }

        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGenerateServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);

        switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMes);
                return CodeFileSaverExecutor.executorSaver(htmlCodeResult, codeGenTypeEnum, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMes);
                return CodeFileSaverExecutor.executorSaver(multiFileCodeResult, codeGenTypeEnum, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型-" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMessage);
            }
        }

    }

    /**
     * 统一生成最终的结果，将重复的逻辑抽取，使用了两个执行器，一个解析字符串，一个存入文件
     *
     * @param stringFlux
     * @param codeGenTypeEnum
     * @return
     */
    private Flux<String> processCodeStream(Flux<String> stringFlux, CodeGenTypeEnum codeGenTypeEnum, long appId) {
        StringBuilder builder = new StringBuilder();
        return stringFlux.doOnNext(builder::append)
                .doOnComplete(() -> {
                    try {
                        Object result = CodeParserExecutor.executeParse(builder.toString(), codeGenTypeEnum);
                        File file = CodeFileSaverExecutor.executorSaver(result, codeGenTypeEnum, appId);
                        log.info("文件保存成功，路径为：{}", file.getPath());
                    } catch (Exception e) {
                        log.error("保存失败，{}", e.getMessage());
                    }
                });
    }


}
