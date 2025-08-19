package com.ckrey.ckreycodemother.core.handler;

import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.exception.ThrowUtils;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.ckrey.ckreycodemother.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
@Slf4j
public class SimpleTextStreamHandler {

    public Flux<String> handle(Flux<String> stringFlux, Long appId, ChatHistoryService chatHistoryService, User loginUser) {

        StringBuffer stringBuffer = new StringBuffer();
        return stringFlux.doOnNext(stringBuffer::append)
                .doOnComplete(() -> {
                    String history = stringBuffer.toString();
                    boolean b = chatHistoryService.addChatMessage(appId, history, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                    log.error("添加历史数据失败");
                    ThrowUtils.throwIf(!b,ErrorCode.OPERATION_ERROR,"添加历史数据失败！");
                }).doOnError(error -> {
                    String aiResponse = stringBuffer.toString();
                    chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                    error.printStackTrace();
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "ai 回复失败" + error.getMessage());
                });
    }


}
