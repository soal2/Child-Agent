package com.example.child_agent_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.ImageUploadResult;
import com.example.child_agent_back.model.ResponseResult;
import com.example.child_agent_back.service.ImageService;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * 图片上传接口
     * POST /api/image/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseResult<ImageUploadResult>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            ImageUploadResult result = imageService.uploadImage(file);
            return ResponseEntity.ok(ResponseResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseResult.fail(500, e.getMessage()));
        }
    }
}