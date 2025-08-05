package com.ckrey.ckreycodemother.common;

import com.ckrey.ckreycodemother.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BaseReponse <T> implements Serializable {

    private Integer code;

    private T data;

    private String message;


    public BaseReponse(ErrorCode errorCode){
       this(errorCode.getCode(),null,errorCode.getMessage());
    }


}
