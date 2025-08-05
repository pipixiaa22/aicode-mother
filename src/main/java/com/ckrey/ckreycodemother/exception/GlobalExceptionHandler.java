package com.ckrey.ckreycodemother.exception;

import com.ckrey.ckreycodemother.common.BaseReponse;
import com.ckrey.ckreycodemother.common.ReponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseReponse<?> handler(BusinessException e){
        log.error(e.getStackTrace().toString());
        return ReponseUtil.error(e);
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseReponse<?> handler(RuntimeException e){
        log.error(e.getMessage());
        return ReponseUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }


}
