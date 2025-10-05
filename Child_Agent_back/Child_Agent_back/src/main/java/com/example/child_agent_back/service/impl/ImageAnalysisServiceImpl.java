package com.example.child_agent_back.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.TosClientException;
import com.volcengine.tos.TosServerException;
import com.volcengine.tos.model.object.PreSignedURLInput;
import com.volcengine.tos.model.object.PreSignedURLOutput;
import com.volcengine.tos.model.object.PutObjectInput;
import com.volcengine.tos.model.object.PutObjectOutput;

import com.example.child_agent_back.config.ApiProperties;
import com.example.child_agent_back.service.ImageAnalysisService;

/**
 * 图片分析服务实现类
 * 实现豆包视觉理解模型API调用
 */
@Service
public class ImageAnalysisServiceImpl implements ImageAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(ImageAnalysisServiceImpl.class);

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private RestTemplate restTemplate;

    private boolean useMockImplementation = true; // 默认使用模拟实现
    private final Random random = new Random();
    private final String tempDir = System.getProperty("java.io.tmpdir");

    /**
     * 初始化服务
     */
    @PostConstruct
    public void init() {
        // 检查API配置是否有效，有效则使用实际实现
        useMockImplementation = !apiProperties.getImage().getAnalyze().isValid();
    }

    /**
     * 分析图片
     * 
     * @param imageUrl 图片URL
     * @param prompt 提示词
     * @return 分析结果
     */
    @Override
    public String analyzeImageByUrl(String imageUrl, String prompt) throws Exception {
        // 添加URL验证
        validateImageUrl(imageUrl);
        
        if (useMockImplementation) {
            return mockAnalyzeImage(imageUrl, prompt);
        }
        return analyzeImageWithApi(imageUrl, prompt);
    }

    /**
     * 验证图片URL的有效性
     * 
     * @param imageUrl 图片URL
     * @throws Exception 当URL无效时抛出异常
     */
    private void validateImageUrl(String imageUrl) throws Exception {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new Exception("图片URL不能为空");
        }
        
        // 验证URL格式
        try {
            new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new Exception("图片URL格式不正确");
        }
        
        // 验证URL可访问性
        if (!isUrlAccessible(imageUrl)) {
            throw new Exception("图片URL无法访问");
        }
        
        // 验证URL是否指向有效的图片
        if (!isImageUrl(imageUrl)) {
            throw new Exception("URL不是有效的图片地址");
        }
    }
    
    /**
     * 检查URL是否可访问
     * 
     * @param url 要检查的URL
     * @return 如果URL可访问返回true，否则返回false
     */
    private boolean isUrlAccessible(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查URL是否指向有效的图片
     * 
     * @param url 要检查的URL
     * @return 如果URL指向有效图片返回true，否则返回false
     */
    private boolean isImageUrl(String url) {
        // 检查URL后缀
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"};
        String lowerUrl = url.toLowerCase();
        
        for (String extension : imageExtensions) {
            if (lowerUrl.endsWith("." + extension)) {
                return true;
            }
        }
        
        // 如果没有文件扩展名，尝试检查Content-Type
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            String contentType = connection.getContentType();
            return contentType != null && contentType.startsWith("image/");
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public String analyzeImageByFile(MultipartFile imageFile, String prompt) throws Exception {
        if (useMockImplementation) {
            return mockAnalyzeImage("local-file", prompt);
        }
        
        // 检查TOS配置是否有效
        ApiProperties.ImageApi.TosApi tosConfig = apiProperties.getImage().getTos();
        if (!tosConfig.isValid()) {
            // TOS配置无效，使用base64方式
            return analyzeImageWithBase64(imageFile, prompt);
        }
        
        // TOS配置有效，使用TOS上传方式
        return analyzeImageWithTos(imageFile, prompt);
    }

    /**
     * 使用API分析图片
     */
    private String analyzeImageWithApi(String imageUrl, String prompt) throws Exception {
        ApiProperties.ImageApi.AnalyzeApi imageConfig = apiProperties.getImage().getAnalyze();
        
        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + imageConfig.getApiKey());
            
            // 创建请求体
            Map<String, Object> requestBody = new HashMap<>();
            // 检查是否在ApiProperties中添加了model属性，如果没有则保留硬编码
            requestBody.put("model", imageConfig.getModel() != null ? imageConfig.getModel() : "ep-20240610154452-x6w7g");
            
            // 创建消息数组
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            
            // 创建多内容数组（根据官方API格式要求）
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, String> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", imageUrl);
            imageContent.put("image_url", imageUrlMap);
            
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", prompt);
            
            // 按照官方格式使用content字段，顺序为[image_content, text_content]
            message.put("content", new Object[]{imageContent, textContent});
            requestBody.put("messages", new Object[]{message});
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 添加请求日志（只记录请求基本信息，不记录敏感数据和大图片内容）
            logger.info("准备调用图片分析API，模型: {}, 终端点: {}", requestBody.get("model"), imageConfig.getEndpoint());
            logger.debug("图片分析请求参数: prompt={}", prompt);
            
            // 发送请求
            Map<String, Object> response = restTemplate.exchange(
                    imageConfig.getEndpoint(),
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            ).getBody();
            
            // 记录完整响应内容（用于调试）
            logger.debug("API响应内容: {}", response);
            
            // 处理响应结果 - 更健壮的响应处理逻辑
            if (response != null) {
                logger.info("接收到API响应，包含的键: {}", response.keySet());
                
                // 检查是否有choices字段（标准OpenAI格式）
                if (response.containsKey("choices")) {
                    Object choicesObj = response.get("choices");
                    
                    // 处理JSON数组转换问题
                    List<?> choicesList = null;
                    if (choicesObj instanceof List) {
                        choicesList = (List<?>) choicesObj;
                    } else if (choicesObj instanceof Object[]) {
                        choicesList = Arrays.asList((Object[]) choicesObj);
                    }
                    
                    if (choicesList != null && !choicesList.isEmpty()) {
                        Object firstChoice = choicesList.get(0);
                        if (firstChoice instanceof Map) {
                            Map<?, ?> choice = (Map<?, ?>) firstChoice;
                            if (choice.containsKey("message") && choice.get("message") instanceof Map) {
                                Map<?, ?> messageObj = (Map<?, ?>) choice.get("message");
                                if (messageObj.containsKey("content")) {
                                    logger.info("成功从响应中提取内容");
                                    return messageObj.get("content").toString();
                                } else {
                                    logger.warn("响应中没有content字段");
                                }
                            } else {
                                logger.warn("响应中没有message字段或message不是Map类型");
                            }
                        } else {
                            logger.warn("choices数组的第一个元素不是Map类型");
                        }
                    } else {
                        logger.warn("响应中的choices为空");
                    }
                } else {
                    logger.warn("响应中没有choices字段");
                    
                    // 检查是否有直接的content字段（某些API可能使用不同格式）
                    if (response.containsKey("content")) {
                        logger.info("从响应中直接提取content字段");
                        return response.get("content").toString();
                    }
                }
            } else {
                logger.warn("API返回的响应为空");
            }
            throw new Exception("API返回结果为空或格式不符合预期");
        } catch (Exception e) {
            throw new Exception("调用豆包视觉理解模型API失败: " + e.getMessage());
        }
    }

    /**
     * 使用TOS上传图片并分析
     */
    private String analyzeImageWithTos(MultipartFile imageFile, String prompt) throws Exception {
        ApiProperties.ImageApi.TosApi tosConfig = apiProperties.getImage().getTos();
        
        try {
            // 1. 生成临时文件
            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "jpg";
            String objectKey = "images/" + UUID.randomUUID() + "." + extension;
            String tempFilePath = tempDir + File.separator + UUID.randomUUID() + "." + extension;
            
            // 保存临时文件
            File tempFile = new File(tempFilePath);
            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 2. 压缩图片
            String compressedFilePath = tempDir + File.separator + UUID.randomUUID() + "." + extension;
            compressImage(tempFilePath, compressedFilePath, 1024 * 1024); // 压缩到1MB左右
            
            // 3. 上传到TOS
            uploadImageToTOS(compressedFilePath, tosConfig.getBucketName(), objectKey);
            
            // 4. 获取预签名URL
            String imageUrl = getPreSignedURL(tosConfig.getBucketName(), objectKey);
            
            // 5. 调用图片分析API
            String result = analyzeImageWithApi(imageUrl, prompt);
            
            // 6. 清理临时文件
            try {
                Files.deleteIfExists(new File(tempFilePath).toPath());
                Files.deleteIfExists(new File(compressedFilePath).toPath());
            } catch (IOException e) {
                // 记录日志但不影响功能
                System.err.println("清理临时文件失败: " + e.getMessage());
            }
            
            return result;
        } catch (Exception e) {
            throw new Exception("使用TOS上传图片并分析失败: " + e.getMessage());
        }
    }

    /**
     * 使用Base64分析图片
     */
    private String analyzeImageWithBase64(MultipartFile imageFile, String prompt) throws Exception {
        try {
            // 转换为Base64编码的Data URL
            String base64Image = convertToBase64DataUrl(imageFile);
            
            // 调用API分析
            return analyzeImageWithApi(base64Image, prompt);
        } catch (Exception e) {
            throw new Exception("使用Base64分析图片失败: " + e.getMessage());
        }
    }

    /**
     * 将MultipartFile转换为Base64编码的Data URL
     */
    private String convertToBase64DataUrl(MultipartFile imageFile) throws IOException, Exception {
        // 检查文件大小（限制为5MB）
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (imageFile.getSize() > maxSize) {
            throw new Exception("图片大小超过限制（5MB），请上传较小的图片");
        }
        
        // 检查文件类型
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("上传的文件不是有效的图片类型");
        }
        
        // 获取文件扩展名
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        
        // 确保是支持的图片格式
        String[] supportedFormats = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        boolean isSupported = false;
        for (String format : supportedFormats) {
            if (format.equals(extension)) {
                isSupported = true;
                break;
            }
        }
        
        if (!isSupported) {
            throw new Exception("不支持的图片格式，请上传jpg、png、gif、bmp或webp格式的图片");
        }
        
        // 生成临时文件路径用于压缩
        String tempFilePath = tempDir + File.separator + UUID.randomUUID() + "." + extension;
        String compressedFilePath = tempDir + File.separator + UUID.randomUUID() + "." + extension;
        
        try {
            // 保存临时文件
            File tempFile = new File(tempFilePath);
            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 压缩图片（限制为2MB）
            compressImage(tempFilePath, compressedFilePath, 2 * 1024 * 1024);
            
            // 读取压缩后的文件并转换为Base64
            byte[] bytes = Files.readAllBytes(new File(compressedFilePath).toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            
            // 检查Base64字符串大小（限制为3MB，因为Base64会增加约33%的大小）
            if (base64.length() > 3 * 1024 * 1024) {
                throw new Exception("转换后的图片数据过大，建议使用更小的图片");
            }
            
            logger.info("成功将图片转换为Base64编码，原始大小: {}KB，压缩后大小: {}KB", 
                        imageFile.getSize() / 1024, bytes.length / 1024);
            
            return "data:" + contentType + ";base64," + base64;
        } finally {
            // 清理临时文件
            try {
                Files.deleteIfExists(new File(tempFilePath).toPath());
                Files.deleteIfExists(new File(compressedFilePath).toPath());
            } catch (IOException e) {
                logger.warn("清理临时文件失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 压缩图片
     */
    private void compressImage(String inputImagePath, String outputImagePath, long targetSizeInBytes) throws IOException {
        File inputFile = new File(inputImagePath);
        File outputFile = new File(outputImagePath);
        
        // 如果原始文件已经小于目标大小，直接复制
        if (inputFile.length() <= targetSizeInBytes) {
            Files.copy(inputFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        
        BufferedImage image = ImageIO.read(inputFile);
        String format = getImageFormat(inputImagePath);
        
        // 质量压缩
        float quality = 0.9f;
        while (outputFile.length() > targetSizeInBytes && quality > 0.1f) {
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(quality);
                }
                
                writer.setOutput(ios);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                writer.dispose();
            }
            
            quality -= 0.1f;
        }
        
        // 如果质量压缩后仍然太大，进行尺寸压缩
        if (outputFile.length() > targetSizeInBytes) {
            BufferedImage scaledImage = scaleImage(image, targetSizeInBytes);
            ImageIO.write(scaledImage, format, outputFile);
        }
    }

    /**
     * 缩放图片
     */
    private BufferedImage scaleImage(BufferedImage originalImage, long targetSizeInBytes) {
        // 简单的缩放逻辑，实际应用中可以根据需要调整
        int targetWidth = originalImage.getWidth() / 2;
        int targetHeight = originalImage.getHeight() / 2;
        
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        scaledImage.getGraphics().drawImage(
                originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH),
                0, 0, null
        );
        
        return scaledImage;
    }

    /**
     * 获取图片格式
     */
    private String getImageFormat(String imagePath) {
        String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") ? "jpeg" : extension;
    }

    /**
     * 上传图片到TOS
     */
    private void uploadImageToTOS(String filePath, String bucketName, String objectKey) throws Exception {
        ApiProperties.ImageApi.TosApi tosConfig = apiProperties.getImage().getTos();
        
        // 创建TOS客户端 - 兼容2.8.8版本
        TOSV2 tos = new TOSV2ClientBuilder().build(
                tosConfig.getRegion(),
                tosConfig.getEndpoint(),
                tosConfig.getAccessKey(),
                tosConfig.getSecretKey()
        );
        
        try {
            // 上传文件 - 兼容2.8.8版本
            File file = new File(filePath);
            try (FileInputStream inputStream = new FileInputStream(file)) {
                PutObjectInput input = new PutObjectInput().setBucket(bucketName).setKey(objectKey)
                        .setContent(inputStream).setContentLength(file.length());
                PutObjectOutput output = tos.putObject(input);
            }
        } catch (TosClientException e) {
            throw new Exception("上传图片到TOS客户端异常: " + e.getMessage());
        } catch (TosServerException e) {
            throw new Exception("上传图片到TOS服务端异常: " + e.getMessage());
        } catch (IOException e) {
            throw new Exception("读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取预签名URL
     */
    private String getPreSignedURL(String bucketName, String objectKey) throws Exception {
        ApiProperties.ImageApi.TosApi tosConfig = apiProperties.getImage().getTos();
        
        // 创建TOS客户端 - 兼容2.8.8版本
        TOSV2 tos = new TOSV2ClientBuilder().build(
                tosConfig.getRegion(),
                tosConfig.getEndpoint(),
                tosConfig.getAccessKey(),
                tosConfig.getSecretKey()
        );
        
        try {
            // 生成预签名URL - 兼容2.8.8版本
            PreSignedURLInput input = new PreSignedURLInput().setBucket(bucketName)
                    .setKey(objectKey)
                    .setHttpMethod(com.volcengine.tos.comm.HttpMethod.GET)
                    .setExpires(tosConfig.getExpireTime());
            PreSignedURLOutput output = tos.preSignedURL(input);
            
            return output.getSignedUrl();
        } catch (TosClientException e) {
            throw new Exception("获取预签名URL客户端异常: " + e.getMessage());
        } catch (TosServerException e) {
            throw new Exception("获取预签名URL服务端异常: " + e.getMessage());
        }
    }

    /**
     * 模拟分析图片的实现
     * 用于测试或API配置无效的情况
     */
    private String mockAnalyzeImage(String imageUrl, String prompt) {
        // 模拟不同类型的图片分析结果
        String[] mockResults = {
            "这张图片展示了一只可爱的小猫，它正蜷缩在沙发上睡觉。小猫的毛色是灰色和白色相间的，看起来非常柔软。",
            "图片中是一片美丽的风景，有蓝天、白云和青山绿水。远处的山峰被云雾环绕，显得格外壮观。",
            "这是一张关于美食的图片，展示了一道精心制作的菜肴。菜品色彩丰富，看起来非常美味可口。",
            "图片中是一个现代化的城市景观，高楼大厦林立，街道上车辆川流不息，展现了城市的繁华与活力。",
            "这张图片记录了一个温馨的家庭场景，家人围坐在餐桌旁共进晚餐，气氛融洽而幸福。"
        };
        
        // 随机选择一个结果
        int index = random.nextInt(mockResults.length);
        return "[模拟结果] " + mockResults[index];
    }
}