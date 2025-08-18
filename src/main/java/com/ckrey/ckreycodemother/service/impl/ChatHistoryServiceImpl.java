package com.ckrey.ckreycodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.exception.ThrowUtils;
import com.ckrey.ckreycodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ckrey.ckreycodemother.model.entity.App;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.ckrey.ckreycodemother.model.enums.UserRoleEnum;
import com.ckrey.ckreycodemother.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ckrey.ckreycodemother.model.entity.ChatHistory;
import com.ckrey.ckreycodemother.mapper.ChatHistoryMapper;
import com.ckrey.ckreycodemother.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author ckrey
 * @since 2025-08-15
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    @Lazy
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


    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq("id", id)
                .like("message", message)
                .eq("\"messageType\"", messageType)  // 添加双引号
                .eq("\"appId\"", appId)              // 添加双引号
                .eq("\"userId\"", userId);           // 添加双引号
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("\"createTime\"", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderBy("\"createTime\"", false);
        }
        return queryWrapper;
    }


    @Override
    public Page<ChatHistory> getChatHistoryPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser){

        //参数校验
        ThrowUtils.throwIf(appId == null || appId <=0,ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(pageSize <0 || pageSize >50,ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null,ErrorCode.PARAMS_ERROR);


        App app = appService.getById(appId);
        //只有本人或者管理员可以查看
        boolean isAdmin = loginUser.getUserrole().equals(UserRoleEnum.ADMIN.getRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isCreator && !isAdmin,ErrorCode.NO_AUTH_ERROR);
        ChatHistoryQueryRequest chatHistoryQueryRequest = new ChatHistoryQueryRequest();
        chatHistoryQueryRequest.setAppId(appId);
        chatHistoryQueryRequest.setLastCreateTime(lastCreateTime);
        chatHistoryQueryRequest.setUserId(loginUser.getId());

        QueryWrapper queryWrapper = this.getQueryWrapper(chatHistoryQueryRequest);
        //todo 一直从第一页开始查询，很简单理解，因为是游标查询，查询条件是改变的
        return this.page(Page.of(1,pageSize),queryWrapper);

    }

    @Override
    public int loadHistory(Long appId, ChatMemory chatMemory,int count){
        try {
            QueryWrapper wrapper = new QueryWrapper().eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime,false)
                    .limit(1,count);
            List<ChatHistory> list = this.list(wrapper);

            list.reversed();

            chatMemory.clear();

            for (ChatHistory chatHistory : list) {
                if (ChatHistoryMessageTypeEnum.AI.getValue().equals(chatHistory.getMessageType())){
                    chatMemory.add(AiMessage.from(chatHistory.getMessage()));
                }
                else if (ChatHistoryMessageTypeEnum.USER.getValue().equals(chatHistory.getMessageType())){
                    chatMemory.add(UserMessage.from(chatHistory.getMessage()));
                }
            }

            log.info("成功加载appId:{}的历史对话记录{}条",appId,list.size());
            return list.size();
        } catch (Exception e) {
            log.error("加载历史对话记录失败，appId为{},原因为：{}", appId,e.getMessage());
            return 0;
        }

    }


}
