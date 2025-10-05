package com.example.child_agent_back.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class FileUploadUtils {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.port}")
    private String serverPort;
    
    // 获取应用程序根目录
    private String getApplicationRootDir() {
        try {
            return new File("").getAbsolutePath();
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }

    /**
     * 保存文件并返回文件路径
     */
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        // 创建目录结构（年/月/日/子目录）
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 获取绝对上传目录路径
        String rootDir = getApplicationRootDir();
        String fullDirPath = rootDir + File.separator + uploadDir + File.separator + dateDir + File.separator + subDir;
        
        File dir = new File(fullDirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IOException("无法创建目录: " + fullDirPath);
            }
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        File dest = new File(fullDirPath + File.separator + fileName);
        file.transferTo(dest);

        // 返回相对路径
        return dateDir + File.separator + subDir + File.separator + fileName;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成文件访问URL
     */
    public String generateFileUrl(String relativePath) {
        // 实际部署时需要修改为实际的域名
        return "http://localhost:" + serverPort + File.separator + "files" + File.separator + relativePath;
    }
}