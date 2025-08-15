package com.ckrey.ckreycodemother.service.impl;

import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.exception.ThrowUtils;
import com.ckrey.ckreycodemother.model.entity.App;
import com.ckrey.ckreycodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.ckrey.ckreycodemother.service.AppService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ckrey.ckreycodemother.model.entity.ChatHistory;
import com.ckrey.ckreycodemother.mapper.ChatHistoryMapper;
import com.ckrey.ckreycodemother.service.ChatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author ckrey
 * @since 2025-08-15
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    private AppService appService;

    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {

        App app = appService.getById(appId);

        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }


        if (!app.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        ChatHistoryMessageTypeEnum enumByValue = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ChatHistory chatHistory = ChatHistory.builder().appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType).build();


       return this.save(chatHistory);
    }


    @Override
    public boolean  deleteChatMessage(Long appId) {
        ThrowUtils.throwIf(appId == null || appId < 0 ,ErrorCode.PARAMS_ERROR,"应用id不能小于零");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(ChatHistory::getAppId,appId);
        return this.remove(queryWrapper);
    }
}
