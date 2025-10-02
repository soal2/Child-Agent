# 后端API测试指南

本文档提供了Child_Agent_back项目后端各模块的测试指南，包括测试命令、请求结构体、保存位置和返回示例。

## 准备工作

在测试前，请确保：
1. 后端服务已成功启动，默认运行在 `http://localhost:8080`
2. 已安装 `curl` 工具（Windows 10/11 已内置，或可从官网下载）
3. 对于文件上传测试，请准备好相应的测试文件

## 1. 聊天接口测试

### 接口信息
- **URL**: `/api/chat/message`
- **方法**: POST
- **描述**: 智能对话接口，处理文本消息并返回智能回复

### 请求结构体
```json
{
  "message": "文本消息内容",
  "type": "text",
  "timestamp": "可选的时间戳，格式：yyyy-MM-dd HH:mm:ss"
}
```

### 测试命令
```bash
curl -X POST -H "Content-Type: application/json" -d '{"message":"你好","type":"text","timestamp":"2025-10-02 12:00:00"}' http://localhost:8080/api/chat/message
```

### 返回示例
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "response": "你好！我是智能助手，很高兴为你服务。",
    "timestamp": "2025-10-02 12:00:01"
  }
}
```

## 2. 图片上传接口测试

### 接口信息
- **URL**: `/api/image/upload`
- **方法**: POST
- **描述**: 上传图片文件并返回文件访问URL

### 请求参数
- `file`: 图片文件（使用multipart/form-data格式）

### 测试命令
```bash
curl -X POST -F "file=@C:\path\to\your\test.png" http://localhost:8080/api/image/upload
```

### 保存位置
上传的图片文件将保存在应用程序根目录下的 `uploads` 文件夹中，采用以下目录结构：
```
uploads/年/月/日/images/唯一文件名.png
```
例如：`uploads/2025/10/02/images/a82592b1-b10f-487f-be67-49dcf953a9fb.png`

### 返回示例
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "url": "http://localhost:8080/files/2025/10/02/images/a82592b1-b10f-487f-be67-49dcf953a9fb.png",
    "path": "2025/10/02/images/a82592b1-b10f-487f-be67-49dcf953a9fb.png"
  }
}
```

## 3. 语音识别接口测试

### 接口信息
- **URL**: `/api/voice/recognize`
- **方法**: POST
- **描述**: 上传语音文件并返回识别结果（当前为模拟实现）

### 请求参数
- `file`: 语音文件（使用multipart/form-data格式）

### 测试命令
```bash
curl -X POST -F "file=@C:\path\to\your\test.wav" http://localhost:8080/api/voice/recognize
```

### 返回示例
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "text": "你好，我是智能助手，请问有什么可以帮助你的？",
    "timestamp": "2025-10-02 12:01:30"
  }
}
```

> **注意**：当前实现为模拟实现，会随机返回预设的文本结果，不依赖实际的语音文件内容。

## 4. 组合消息处理接口测试

### 接口信息
- **URL**: `/api/message/combined`
- **方法**: POST
- **描述**: 同时处理图片和语音文件，综合两者信息生成智能回复

### 请求参数
- `imageFile`: 图片文件
- `voiceFile`: 语音文件
- `voiceDuration`: 可选，语音时长

### 测试命令
```bash
curl -X POST -F "imageFile=@C:\path\to\your\test.png" -F "voiceFile=@C:\path\to\your\test.wav" -F "voiceDuration=10" http://localhost:8080/api/message/combined
```

### 保存位置
- 图片文件保存在 `uploads/年/月/日/images/` 目录下
- 语音文件不保存（仅用于识别）

### 返回示例
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "response": "你发送了一张图片，并且询问了天气情况。根据你的图片和语音内容，我可以为你提供相关信息。",
    "timestamp": "2025-10-02 12:02:45"
  }
}
```

## 5. 全局异常处理测试

### 文件大小超限测试
当上传的文件超过配置的大小限制（10MB）时，系统会返回文件大小超限的错误：

```json
{
  "code": 413,
  "message": "文件大小超出限制",
  "data": null
}
```

### 其他错误测试
当发生其他错误时，系统会返回内部错误信息：

```json
{
  "code": 500,
  "message": "系统内部错误：具体错误信息",
  "data": null
}
```

## 6. 测试注意事项

1. **模拟实现**：目前大部分API功能为模拟实现，不依赖真实的第三方API服务
2. **端口配置**：如果服务运行在非默认端口，请修改测试命令中的端口号
3. **文件路径**：测试文件上传时，请确保提供正确的文件路径
4. **响应格式**：所有API的响应都遵循统一的`ResponseResult`格式
5. **日志查看**：可以通过控制台日志查看API的详细执行过程

## 7. 常见问题排查

- **连接拒绝错误**：检查服务是否已启动，端口是否正确
- **文件上传错误**：确保文件路径正确，文件格式受支持
- **500错误**：查看控制台日志获取详细错误信息
- **权限问题**：确保应用程序有文件读写权限

---

更新时间：2025-10-02