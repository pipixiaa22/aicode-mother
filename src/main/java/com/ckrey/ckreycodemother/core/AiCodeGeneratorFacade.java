package com.ckrey.ckreycodemother.core;

import cn.hutool.core.util.StrUtil;
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
    private AiCodeGeneratorService aiCodeGeneratorService;


    public Flux<String> codeGenerateAndSaveStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提示词不能为空");
        }

        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        Flux<String> stringFlux = null;

        switch (codeGenTypeEnum) {
            case HTML -> {
                stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                return processCodeStream(stringFlux, CodeGenTypeEnum.HTML);
            }
            case MULTI_FILE -> {
                stringFlux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                return processCodeStream(stringFlux, CodeGenTypeEnum.MULTI_FILE);
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持的类型");
        }

    }

    public File generateSaveCode(String userMes, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数枚举为空");
        }

        switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMes);
                return CodeFileSaverExecutor.executorSaver(htmlCodeResult, codeGenTypeEnum);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMes);
                return CodeFileSaverExecutor.executorSaver(multiFileCodeResult, codeGenTypeEnum);
            }
            default -> {
                String errorMessage = "不支持的生成类型-" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMessage);
            }
        }

    }

    /**
     * 统一生成最终的结果，将重复的逻辑抽取，使用了两个执行器
     *
     * @param stringFlux
     * @param codeGenTypeEnum
     * @return
     */
    private Flux<String> processCodeStream(Flux<String> stringFlux, CodeGenTypeEnum codeGenTypeEnum) {
        StringBuilder builder = new StringBuilder();
        return stringFlux.doOnNext(builder::append).doOnComplete(() -> {
            try {
                Object result = CodeParserExecutor.executeParse(builder.toString(), codeGenTypeEnum);
                File file = CodeFileSaverExecutor.executorSaver(result, codeGenTypeEnum);
                log.info("文件保存成功，路径为：{}", file.getPath());
            } catch (Exception e) {
                log.error("保存失败，{}", e.getMessage());
            }
        });
    }


}
