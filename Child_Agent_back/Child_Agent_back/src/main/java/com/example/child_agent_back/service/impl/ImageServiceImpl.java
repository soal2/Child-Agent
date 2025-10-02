package com.example.child_agent_back.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.child_agent_back.model.ImageUploadResult;
import com.example.child_agent_back.service.ImageService;
import com.example.child_agent_back.util.FileUploadUtils;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private FileUploadUtils fileUploadUtils;

    @Override
    public ImageUploadResult uploadImage(MultipartFile file) throws Exception {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new Exception("图片文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("请上传有效的图片文件");
        }

        // 保存文件到图片目录
        String relativePath = fileUploadUtils.saveFile(file, "images");
        
        // 生成文件访问URL
        String url = fileUploadUtils.generateFileUrl(relativePath);
        
        return new ImageUploadResult(url, relativePath);
    }
}