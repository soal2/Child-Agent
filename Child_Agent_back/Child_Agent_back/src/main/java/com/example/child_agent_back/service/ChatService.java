package com.example.child_agent_back.service;

import com.example.child_agent_back.model.ChatMessage;
import com.example.child_agent_back.model.ChatResponse;

public interface ChatService {
    /**
     * 处理用户发送的消息并返回智能回复
     * @param chatMessage 用户发送的消息
     * @return 智能回复内容
     */
    ChatResponse processChatMessage(ChatMessage chatMessage) throws Exception;
}