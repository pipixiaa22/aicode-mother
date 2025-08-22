package com.ckrey.ckreycodemother.ai;

import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

public interface AiCodeRouterService {

    /**
     * 根据用户需求智能选择代码类型
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "/prompt/codegen-router-system-prompt.txt")
    CodeGenTypeEnum router(String userMessage);


}
