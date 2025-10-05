package com.example.child_agent_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.child_agent_back.model.ChatMessage;
import com.example.child_agent_back.model.ChatResponse;
import com.example.child_agent_back.model.ResponseResult;
import com.example.child_agent_back.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 智能对话接口
     * POST /api/chat/message
     */
    @PostMapping("/message")
    public ResponseEntity<ResponseResult<ChatResponse>> processChatMessage(@RequestBody ChatMessage chatMessage) {
        try {
            ChatResponse response = chatService.processChatMessage(chatMessage);
            return ResponseEntity.ok(ResponseResult.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseResult.fail(500, e.getMessage()));
        }
    }
}