package com.ckrey.ckreycodemother.service;

import com.ckrey.ckreycodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ckrey.ckreycodemother.model.entity.ChatHistory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author ckrey
 * @since 2025-08-15
 */
public interface ChatHistoryService extends IService<ChatHistory> {



    boolean addChatMessage(Long appId, String message, String messageType, Long userId);



    boolean deleteChatMessage(Long appId);

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> getChatHistoryPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);
}
