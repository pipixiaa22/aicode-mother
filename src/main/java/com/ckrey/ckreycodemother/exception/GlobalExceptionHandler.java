package com.ckrey.ckreycodemother.exception;

import com.ckrey.ckreycodemother.common.BaseResponse;
import com.ckrey.ckreycodemother.common.ResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> handler(BusinessException e){
        log.error("发生异常",e);
        return ResponseUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> handler(RuntimeException e){
        log.error(e.getMessage());
        return ResponseUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }


}
