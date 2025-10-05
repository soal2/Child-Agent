package com.example.child_agent_back.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 图片分析服务接口
 * 用于调用豆包视觉理解模型API进行图片内容分析
 */
public interface ImageAnalysisService {
    
    /**
     * 分析图片内容
     * @param imageUrl 图片URL地址
     * @param prompt 提示文本
     * @return 图片分析结果
     */
    String analyzeImageByUrl(String imageUrl, String prompt) throws Exception;
    
    /**
     * 通过MultipartFile直接分析图片
     * @param imageFile 图片文件
     * @param prompt 提示文本
     * @return 图片分析结果
     */
    String analyzeImageByFile(MultipartFile imageFile, String prompt) throws Exception;
}