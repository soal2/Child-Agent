package com.example.child_agent_back.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.child_agent_back.config.ApiProperties;
import com.example.child_agent_back.model.ChatMessage;
import com.example.child_agent_back.model.ChatResponse;
import com.example.child_agent_back.service.ChatService;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ApiProperties apiProperties;
    
    // 注入大语言模型API客户端（实际类型根据具体实现而定）
    @Autowired(required = false)
    @Qualifier("chatModelClient")
    private Object chatModelClient;

    // 模拟的智能回复库
    private static final Map<String, String[]> MOCK_RESPONSES = new HashMap<>();
    private final Random random = new Random();
    
    private boolean useMockImplementation = true;

    static {
        // 初始化一些预设问答对
        MOCK_RESPONSES.put("你好", new String[]{
                "你好！很高兴为你服务。",
                "你好呀，有什么可以帮助你的吗？",
                "嗨！我是你的智能助手。"
        });
        
        MOCK_RESPONSES.put("你是谁", new String[]{
                "我是你的智能语音助手，专门为儿童设计。",
                "我是一个AI助手，可以回答问题、讲故事。",
                "我是Child-Agent，很高兴认识你！"
        });
        
        MOCK_RESPONSES.put("天气", new String[]{
                "今天天气不错，阳光明媚。",
                "根据天气预报，今天可能会下雨。",
                "天气晴朗，适合外出活动。"
        });
        
        MOCK_RESPONSES.put("故事", new String[]{
                "从前有一个小王子...",
                "想听什么类型的故事呢？童话故事还是科普故事？",
                "我可以给你讲一个关于小动物的故事。"
        });
    }
    
    // 默认回复
    private static final String[] DEFAULT_RESPONSES = {
            "抱歉，我不太理解你的意思。",
            "这个问题有点难，能换个方式问吗？",
            "让我想想...",
            "我正在学习中，这个问题我还回答不了。",
            "你能详细说明一下吗？"
    };

    /**
     * 初始化服务
     * 检查是否有有效的API客户端，如果有则使用真实API，否则使用模拟实现
     */
    @PostConstruct
    public void init() {
        // 检查是否有有效的API客户端和配置
        if (chatModelClient != null && 
            apiProperties.getChat().isValid()) {
            useMockImplementation = false;
        }
    }

    @Override
    public ChatResponse processChatMessage(ChatMessage chatMessage) throws Exception {
        if (chatMessage == null || chatMessage.getMessage() == null || chatMessage.getMessage().trim().isEmpty()) {
            throw new Exception("消息内容不能为空");
        }

        // 获取用户消息
        String userMessage = chatMessage.getMessage().toLowerCase();
        
        String reply;
        if (useMockImplementation) {
            // 使用模拟实现
            reply = generateMockReply(userMessage);
        } else {
            // 使用真实API实现
            reply = generateRealApiReply(userMessage);
        }
        
        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        return new ChatResponse(reply, "text", timestamp);
    }
    
    /**
     * 生成模拟回复
     */
    private String generateMockReply(String userMessage) {
        // 查找匹配的关键词
        for (Map.Entry<String, String[]> entry : MOCK_RESPONSES.entrySet()) {
            if (userMessage.contains(entry.getKey())) {
                String[] responses = entry.getValue();
                return responses[random.nextInt(responses.length)];
            }
        }
        
        // 如果没有匹配的关键词，返回默认回复
        return DEFAULT_RESPONSES[random.nextInt(DEFAULT_RESPONSES.length)];
    }
    
    /**
     * 使用真实大语言模型API生成回复
     * 实际项目中，这里应该调用真实的大语言模型API
     * 例如豆包大模型等
     */
    private String generateRealApiReply(String userMessage) throws Exception {
        // 获取API配置
        ApiProperties.ChatApi config = apiProperties.getChat();
        
        // 示例：使用豆包大模型API
        /*
        // 假设我们有一个豆包客户端的类
        DoubaoClient client = (DoubaoClient) chatModelClient;
        
        // 构建请求参数
        DoubaoRequest request = DoubaoRequest.builder()
                .model(config.getModel())
                .messages(List.of(
                        DoubaoMessage.builder()
                                .role("system")
                                .content("你是一个为儿童设计的智能助手，回答要友好、简单、有趣。")
                                .build(),
                        DoubaoMessage.builder()
                                .role("user")
                                .content(userMessage)
                                .build()
                ))
                .maxTokens(100)
                .temperature(0.7)
                .build();
        
        // 调用API
        DoubaoResponse response = client.complete(request);
        
        // 处理API返回结果
        return response.getResult();
        */
        
        // 当前仍然使用模拟结果，实际集成时需要替换为真实API调用
        return generateMockReply(userMessage);
    }
}