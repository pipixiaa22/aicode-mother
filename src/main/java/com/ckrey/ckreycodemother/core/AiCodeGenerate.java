package com.ckrey.ckreycodemother.core;

import com.ckrey.ckreycodemother.core.parser.CodeParser;
import com.ckrey.ckreycodemother.core.parser.HtmlCodeParser;
import com.ckrey.ckreycodemother.core.parser.MultFileCodeParser;
import com.ckrey.ckreycodemother.core.saver.CodeFileSaverExecutor;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import reactor.core.publisher.Flux;

import java.io.File;


public class AiCodeGenerate {

    private CodeParser codeParser;

//    public AiCodeGenerate(CodeParser codeParser2) {
//        this.codeParser = codeParser2;
//    }

    public File codeGenerateAndSave(String content, CodeGenTypeEnum codeGenTypeEnum) {

        switch (codeGenTypeEnum){
            case HTML -> codeParser = new HtmlCodeParser();
            case MULTI_FILE -> codeParser = new MultFileCodeParser();
        }

        Object result = codeParser.parse(content);
        return CodeFileSaverExecutor.executorSaver(result, codeGenTypeEnum);

    }


}
