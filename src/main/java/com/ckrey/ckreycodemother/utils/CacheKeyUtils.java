package com.ckrey.ckreycodemother.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.util.DigestUtils;

public class CacheKeyUtils {



    public static String getCacheKey(Object obj){
        if (obj == null){
            return DigestUtil.md5Hex("null");
        }
        String jsonStr = JSONUtil.toJsonStr(obj);
        return DigestUtil.md5Hex(jsonStr);
    }
}
