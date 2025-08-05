package com.ckrey.ckreycodemother.exception;

public class ThrowUtil {


    public static void throwif(Boolean flag,ErrorCode errorCode){
        if (flag){
            throw new BusinessException(errorCode);
        }
    }

    public static void throwif(Boolean flag,BusinessException e){
        if (flag){
            throw e;
        }
    }

    public static void throwif(Boolean flag,ErrorCode errorCode,String message){
        if (flag){
            throw new BusinessException(errorCode,message);
        }
    }




}
