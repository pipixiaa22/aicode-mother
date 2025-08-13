package com.ckrey.ckreycodemother.common;

import com.ckrey.ckreycodemother.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {

    private Integer code;

    private T data;

    private String message;


    public BaseResponse(ErrorCode errorCode){
       this(errorCode.getCode(),null,errorCode.getMessage());
    }


}
