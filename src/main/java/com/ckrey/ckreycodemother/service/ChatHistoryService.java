package com.ckrey.ckreycodemother.service;

import com.ckrey.ckreycodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.mybatisflex.core.service.IService;
import com.ckrey.ckreycodemother.model.entity.ChatHistory;

/**
 * 对话历史 服务层。
 *
 * @author ckrey
 * @since 2025-08-15
 */
public interface ChatHistoryService extends IService<ChatHistory> {



    boolean addChatMessage(Long appId, String message, String messageType, Long userId);



    boolean deleteChatMessage(Long appId);
}
