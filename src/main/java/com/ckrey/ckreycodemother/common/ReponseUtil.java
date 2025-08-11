package com.ckrey.ckreycodemother.common;

import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;

public class ReponseUtil {



    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data,"ok");
    }


    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }

    public static <T> BaseResponse<T> error(BusinessException e){
        return new BaseResponse<>(e.getCode(),null,e.getMessage());
    }


    public static <T> BaseResponse<T> error(Integer code, String message){
        return new BaseResponse<>(code,null,message);
    }
}
