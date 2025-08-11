package com.ckrey.ckreycodemother.exception;

public class ThrowUtils {


    public static void throwIf(Boolean flag, ErrorCode errorCode){
        if (flag){
            throw new BusinessException(errorCode);
        }
    }

    public static void throwIf(Boolean flag, BusinessException e){
        if (flag){
            throw e;
        }
    }

    public static void throwIf(Boolean flag, ErrorCode errorCode, String message){
        if (flag){
            throw new BusinessException(errorCode,message);
        }
    }




}
