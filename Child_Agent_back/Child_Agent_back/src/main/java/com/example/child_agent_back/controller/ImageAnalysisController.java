package com.example.child_agent_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.ResponseResult;
import com.example.child_agent_back.service.ImageAnalysisService;

/**
 * 图片分析控制器
 * 提供图片分析相关的API接口
 */
@RestController
@RequestMapping("/api/image/analysis")
public class ImageAnalysisController {

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    /**
     * 通过图片URL分析图片内容
     * GET /api/image/analysis/url
     * 
     * @param imageUrl 图片URL地址
     * @param prompt 提示文本（可选，默认为"这张图片里有什么？"）
     * @return 图片分析结果
     */
    @GetMapping("/url")
    public ResponseEntity<ResponseResult<String>> analyzeImageByUrl(
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam(value = "prompt", required = false, defaultValue = "这张图片里有什么？") String prompt) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseResult.fail(400, "图片URL不能为空"));
            }

            String result = imageAnalysisService.analyzeImageByUrl(imageUrl, prompt);
            return ResponseEntity.ok(ResponseResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseResult.fail(500, "图片分析失败：" + e.getMessage()));
        }
    }

    /**
     * 直接上传图片并分析内容
     * POST /api/image/analysis/file
     * 
     * @param imageFile 图片文件
     * @param prompt 提示文本（可选，默认为"这张图片里有什么？"）
     * @return 图片分析结果
     */
    @PostMapping("/file")
    public ResponseEntity<ResponseResult<String>> analyzeImageByFile(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "prompt", required = false, defaultValue = "这张图片里有什么？") String prompt) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseResult.fail(400, "图片文件不能为空"));
            }

            String result = imageAnalysisService.analyzeImageByFile(imageFile, prompt);
            return ResponseEntity.ok(ResponseResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseResult.fail(500, "图片分析失败：" + e.getMessage()));
        }
    }
}