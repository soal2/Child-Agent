package com.example.child_agent_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.ResponseResult;
import com.example.child_agent_back.model.VoiceRecognitionResult;
import com.example.child_agent_back.service.VoiceService;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    /**
     * 语音识别接口
     * POST /api/voice/recognize
     */
    @PostMapping("/recognize")
    public ResponseEntity<ResponseResult<VoiceRecognitionResult>> recognizeVoice(@RequestParam("file") MultipartFile file) {
        try {
            VoiceRecognitionResult result = voiceService.recognizeVoice(file);
            return ResponseEntity.ok(ResponseResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseResult.fail(500, e.getMessage()));
        }
    }
}