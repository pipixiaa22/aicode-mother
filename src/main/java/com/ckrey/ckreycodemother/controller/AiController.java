package com.ckrey.ckreycodemother.controller;

import com.ckrey.ckreycodemother.ai.AiCodeGeneratorService;
import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import com.ckrey.ckreycodemother.common.BaseResponse;
import com.ckrey.ckreycodemother.common.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiCodeGeneratorService aiChat;

    @PostMapping("/chat")
    public BaseResponse<HtmlCodeResult> chat(String message){
        return ResponseUtil.success(aiChat.generateHtmlCode(message));
    }
}
