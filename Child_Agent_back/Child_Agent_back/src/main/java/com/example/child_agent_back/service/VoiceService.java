package com.example.child_agent_back.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.VoiceRecognitionResult;

public interface VoiceService {
    /**
     * 识别语音文件内容
     * @param file 音频文件
     * @return 识别结果
     */
    VoiceRecognitionResult recognizeVoice(MultipartFile file) throws Exception;
}