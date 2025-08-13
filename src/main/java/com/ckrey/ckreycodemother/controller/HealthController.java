package com.ckrey.ckreycodemother.controller;

import com.ckrey.ckreycodemother.common.BaseResponse;
import com.ckrey.ckreycodemother.common.ResponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/test")
    public BaseResponse<String> test(){
        return ResponseUtil.success("hello");
    }
}
