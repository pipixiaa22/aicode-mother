package com.ckrey.ckreycodemother.core.parser;

import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;

public class CodeParserExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();

    private static final MultFileCodeParser multFileCodeParser = new MultFileCodeParser();



    public static Object executeParse(String content, CodeGenTypeEnum codeGenTypeEnum){
        return switch (codeGenTypeEnum){
            case HTML -> htmlCodeParser.parse(content);
            case MULTI_FILE -> multFileCodeParser.parse(content);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持的代码生成类型");
        };
    }


}
