package com.ckrey.ckreycodemother.controller;

import com.ckrey.ckreycodemother.common.BaseReponse;
import com.ckrey.ckreycodemother.common.ReponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/test")
    public BaseReponse<String> test(){
        return ReponseUtil.success("hello");
    }
}
