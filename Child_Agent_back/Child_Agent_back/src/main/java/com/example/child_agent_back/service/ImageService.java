package com.example.child_agent_back.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.ImageUploadResult;

public interface ImageService {
    /**
     * 上传图片文件
     * @param file 图片文件
     * @return 上传结果，包含URL和路径信息
     */
    ImageUploadResult uploadImage(MultipartFile file) throws Exception;
}