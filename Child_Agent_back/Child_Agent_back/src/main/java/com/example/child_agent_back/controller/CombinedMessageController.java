package com.example.child_agent_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.ChatResponse;
import com.example.child_agent_back.model.ResponseResult;
import com.example.child_agent_back.service.ImageService;
import com.example.child_agent_back.service.VoiceService;
import com.example.child_agent_back.service.ChatService;
import com.example.child_agent_back.model.ChatMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/message")
public class CombinedMessageController {

    @Autowired
    private VoiceService voiceService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private ChatService chatService;

    /**
     * 组合消息处理接口
     * POST /api/message/combined
     */
    @PostMapping("/combined")
    public ResponseEntity<ResponseResult<ChatResponse>> processCombinedMessage(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("voiceFile") MultipartFile voiceFile,
            @RequestParam(value = "voiceDuration", required = false) String voiceDuration) {
        try {
            // 1. 处理语音文件
            String recognizedText = voiceService.recognizeVoice(voiceFile).getText();
            
            // 2. 处理图片文件
            String imageUrl = imageService.uploadImage(imageFile).getUrl();
            
            // 3. 组合处理结果并生成回复
            // 实际项目中，这里应该将图片信息和语音识别结果一起发送给大语言模型
            String combinedMessage = "用户发送了图片，图片URL：" + imageUrl + "\n" +
                                     "语音内容：" + recognizedText;
            
            // 创建聊天消息对象
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(combinedMessage);
            chatMessage.setType("combined");
            chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 获取智能回复
            ChatResponse response = chatService.processChatMessage(chatMessage);
            
            return ResponseEntity.ok(ResponseResult.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseResult.fail(500, e.getMessage()));
        }
    }
}