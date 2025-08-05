package com.ckrey.ckreycodemother.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException{

    private Integer code;

    public BusinessException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = code;
    }
    public BusinessException(ErrorCode errorCode,String message){
        super(message);
        this.code = code;
    }

}
