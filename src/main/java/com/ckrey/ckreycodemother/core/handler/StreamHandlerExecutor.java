package com.ckrey.ckreycodemother.core.handler;

import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import com.ckrey.ckreycodemother.service.ChatHistoryService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
@Component
public class StreamHandlerExecutor {


    private final SimpleTextStreamHandler simpleTextStreamHandler = new SimpleTextStreamHandler();

    private final JsonMessageStreamHandler jsonMessageStreamHandler = new JsonMessageStreamHandler();

    public Flux<String> streamHandler(Flux<String> stringFlux, CodeGenTypeEnum codeGenTypeEnum, ChatHistoryService chatHistoryService,
                                      long appId, User loginUser) {

        //指定执行器，根据枚举类来判断下游对流的处理，即，到底是原生的根据字符串直接拼接并存入数据库
        //还是在vue工程模式下，要经过复杂的解析，因为两个输出的结果不一样，vue工程涉及到ai响应，工具调用过程，工具调用结果

        return switch (codeGenTypeEnum) {
            case VUE_PROJECT -> jsonMessageStreamHandler.handle(stringFlux, chatHistoryService, appId, loginUser);
            case HTML, MULTI_FILE -> simpleTextStreamHandler.handle(stringFlux, appId, chatHistoryService, loginUser);

        };


    }


}
