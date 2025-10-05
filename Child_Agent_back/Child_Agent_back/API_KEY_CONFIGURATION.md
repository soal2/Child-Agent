# API密钥配置指南

本文件提供了如何配置Child-Agent后端服务中使用的各种API密钥的详细指南。

## 配置方式

Child-Agent支持两种方式配置API密钥：

### 1. 环境变量方式（推荐）

在生产环境中，推荐使用环境变量的方式配置API密钥，这样可以避免将敏感信息直接写入配置文件。

需要设置的环境变量包括：

```bash
# 语音识别API相关环境变量
VOICE_RECOGNIZE_API_KEY=your_voice_recognition_api_key
VOICE_RECOGNIZE_APP_ID=your_voice_recognition_app_id
VOICE_RECOGNIZE_SECRET_KEY=your_voice_recognition_secret_key

# 大语言模型API相关环境变量
CHAT_API_KEY=your_doubao_api_key

# 图片分析API相关环境变量
IMAGE_ANALYZE_API_KEY=your_image_analyze_api_key
```

### 2. 配置文件方式

也可以直接在`application.properties`文件中配置API密钥：

```properties
# 语音识别相关配置
av.api.voice.recognize.api-key=your_voice_recognition_api_key
av.api.voice.recognize.app-id=your_voice_recognition_app_id
av.api.voice.recognize.secret-key=your_voice_recognition_secret_key

# 大语言模型相关配置
av.api.chat.api-key=your_doubao_api_key

# 图片分析相关配置
av.api.image.analyze.api-key=your_image_analyze_api_key
```

## 支持的API服务

### 1. 语音识别API

默认配置使用百度语音识别API，配置包括：
- API Key
- App ID
- Secret Key（可选）
- API 端点URL

### 2. 大语言模型API

默认配置使用豆包大模型，配置包括：
- API Key
- 模型名称（默认：doubao-pro）
- API 端点URL

### 3. 图片分析API

默认配置使用百度图像识别API，配置包括：
- API Key
- API 端点URL

## 如何集成其他API提供商

### 修改配置文件

1. 更新`application.properties`中的API端点URL：

```properties
# 例如，修改为讯飞语音识别API
av.api.voice.recognize.endpoint=https://api.xfyun.cn/v1/service/v1/iat

# 例如，修改为其他大语言模型API
av.api.chat.endpoint=https://api.doubao.com/v1/chat/completions
```

### 修改客户端实现

1. 在`pom.xml`中添加对应API提供商的SDK依赖

2. 修改`ApiClientConfig.java`中的客户端初始化代码：

```java
// 例如，修改语音识别客户端为讯飞API
@Bean
public Object voiceRecognitionClient(RestTemplate restTemplate) {
    // 讯飞API客户端初始化代码
    return new IflytekSpeechClient(apiProperties);
}

// 例如，修改大语言模型客户端为豆包API
@Bean
public Object chatModelClient(RestTemplate restTemplate) {
    ApiProperties.ChatApi chatConfig = apiProperties.getChat();
    
    if (!chatConfig.isValid()) {
        return null; // 配置无效时返回null
    }
    
    // 豆包API客户端初始化代码
    // 实际项目中需要根据豆包SDK的要求进行初始化
    return null;
}
```

3. 修改对应服务实现类中的API调用代码

## 安全注意事项

1. **不要将API密钥提交到代码仓库**
   - 使用`.gitignore`文件排除包含敏感信息的配置文件
   - 优先使用环境变量方式配置密钥

2. **限制API密钥的权限范围**
   - 为API密钥设置最小必要的权限
   - 定期轮换API密钥

3. **监控API使用情况**
   - 定期检查API使用日志，及时发现异常访问

4. **考虑使用密钥管理服务**
   - 在企业环境中，可以考虑使用专业的密钥管理服务

## 本地开发和测试

在本地开发和测试阶段，如果没有配置真实的API密钥，系统会自动使用模拟实现：

- 语音识别会返回随机的模拟文本
- 智能对话会使用预设的问答库
- 图片分析会返回模拟的分析结果

这样可以在没有真实API密钥的情况下，仍然能够开发和测试应用的其他功能。