package com.ckrey.ckreycodemother.core.parser;

/**
 * 将字符串解析为对应的类，之所以为泛形，是因为解析的类存在两种情况，其一为html，其二为mult
 * @param <T>
 */
public interface CodeParser<T> {

    T parse(String content);


}
