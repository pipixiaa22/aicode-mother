package com.ckrey.ckreycodemother.common;

import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;

public class ReponseUtil {



    public static <T> BaseReponse<T> success(T data){
        return new BaseReponse<>(ErrorCode.SUCCESS.getCode(), data,"ok");
    }


    public static <T> BaseReponse<T> error(ErrorCode errorCode){
        return new BaseReponse<>(errorCode);
    }

    public static <T> BaseReponse<T> error(ErrorCode errorCode,String message){
        return new BaseReponse<>(errorCode.getCode(),null,message);
    }

    public static <T> BaseReponse<T> error(BusinessException e){
        return new BaseReponse<>(e.getCode(),null,e.getMessage());
    }


    public static <T> BaseReponse<T> error(Integer code,String message){
        return new BaseReponse<>(code,null,message);
    }
}
