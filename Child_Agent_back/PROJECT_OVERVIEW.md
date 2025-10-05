# Child_Agent_back项目介绍

## 项目概述

Child_Agent_back是一个基于Spring Boot框架开发的智能助手后端服务项目，为前端应用提供语音识别、图像处理和智能对话等核心功能支持。项目采用典型的MVC架构设计，具有良好的模块化和可扩展性。

## 目录结构

项目采用标准的Spring Boot项目结构，代码组织清晰，各模块职责明确。主要源代码位于`src/main/java/com/example/child_agent_back/`目录下，按功能模块划分为配置、控制层、服务层、数据模型和工具类等。

```
Child_Agent_back/
├── src/main/java/com/example/child_agent_back/  # 主源码目录
│   ├── config/          # 配置相关类
│   ├── controller/      # 控制器层
│   ├── model/           # 数据模型层
│   ├── service/         # 服务层接口
│   │   └── impl/        # 服务层实现
│   ├── util/            # 工具类
│   └── ChildAgentBackApplication.java  # 应用程序入口
├── src/main/resources/  # 资源文件目录
│   └── application.properties  # 应用配置文件
├── uploads/             # 文件上传目录
├── pom.xml              # Maven依赖管理
├── API_TEST_GUIDE.md    # API测试指南
└── API_KEY_CONFIGURATION.md  # API密钥配置说明
```

## 核心模块与功能

### 1. 配置层 (config包)

配置层负责管理应用程序的各种配置信息和Bean定义，包括API客户端配置、属性配置和Web配置等。

- **ApiProperties.java**：加载和管理应用程序中的各种API配置，如语音识别、大语言模型和图片分析的API密钥、端点等信息。
- **ApiClientConfig.java**：配置各种API客户端实例，用于调用第三方服务（如百度AI、大语言模型等）。
- **WebConfig.java**：配置Web资源处理，主要实现了静态资源的映射，特别是上传文件的访问路径配置。
- **CorsConfig.java**：配置跨域资源共享(CORS)，允许前端应用从不同域访问后端API。

### 2. 控制器层 (controller包)

控制器层负责处理HTTP请求，接收用户输入，调用相应的服务层进行业务处理，并返回处理结果。

- **ChatController.java**：提供智能对话相关的API接口，处理用户发送的文本消息并返回智能回复。
  - 主要接口：`POST /api/chat/message`

- **VoiceController.java**：提供语音识别相关的API接口，接收语音文件并返回识别结果。
  - 主要接口：`POST /api/voice/recognize`

- **ImageController.java**：提供图像处理相关的API接口，接收图片文件并返回访问URL。
  - 主要接口：`POST /api/image/upload`

- **CombinedMessageController.java**：提供组合消息处理的API接口，同时处理图片和语音文件。
  - 主要接口：`POST /api/message/combined`

- **GlobalExceptionHandler.java**：全局异常处理器，统一处理应用程序中的各类异常，返回标准化的错误响应。

### 3. 服务层 (service包)

服务层包含业务逻辑的核心实现，定义了服务接口和具体实现类，负责处理具体的业务需求。

- **ChatService.java**：定义智能对话服务的接口，声明了处理用户消息的核心方法。
- **VoiceService.java**：定义语音识别服务的接口，声明了语音识别的核心方法。
- **ImageService.java**：定义图像处理服务的接口，声明了图片上传的核心方法。
- **impl包**：包含上述服务接口的具体实现类，如`ChatServiceImpl.java`、`VoiceServiceImpl.java`和`ImageServiceImpl.java`，负责实现具体的业务逻辑。

### 4. 数据模型层 (model包)

数据模型层定义了应用程序中使用的各种数据对象，包括请求和响应对象。

- **ChatMessage.java**：表示用户发送的聊天消息的数据模型。
- **ChatResponse.java**：表示系统返回的聊天响应的数据模型。
- **VoiceRecognitionResult.java**：表示语音识别结果的数据模型。
- **ImageUploadResult.java**：表示图片上传结果的数据模型，包含图片URL和存储路径等信息。
- **ResponseResult.java**：通用的API响应封装类，用于统一API的返回格式，包含状态码、消息和数据三个字段。

### 5. 工具类 (util包)

工具类提供了应用程序中常用的工具方法，如文件处理、日期处理等。

- **FileUploadUtils.java**：提供文件上传相关的工具方法，包括文件保存、路径生成和URL生成等功能。

## 核心功能实现

### 1. 语音识别功能

语音识别功能允许用户上传语音文件，系统调用第三方语音识别API（如百度语音识别）进行识别，并返回识别结果。

**主要流程**：
1. 用户通过`/api/voice/recognize`接口上传语音文件
2. 系统验证文件有效性
3. 调用语音识别服务处理文件
4. 返回识别结果

### 2. 图像处理功能

图像处理功能允许用户上传图片文件，系统保存文件并返回可访问的URL地址。

**主要流程**：
1. 用户通过`/api/image/upload`接口上传图片文件
2. 系统验证文件有效性
3. 调用文件上传工具保存文件到服务器
4. 生成可访问的图片URL并返回

### 3. 智能对话功能

智能对话功能允许用户发送文本消息，系统调用大语言模型（如豆包）生成智能回复。

**主要流程**：
1. 用户通过`/api/chat/message`接口发送文本消息
2. 系统验证消息内容
3. 调用智能对话服务处理消息
4. 返回生成的智能回复

### 4. 组合消息处理功能

组合消息处理功能允许用户同时上传图片和语音文件，系统结合两者信息生成综合回复。

**主要流程**：
1. 用户通过`/api/message/combined`接口上传图片和语音文件
2. 系统分别处理图片和语音文件
3. 组合处理结果，调用大语言模型生成综合回复
4. 返回生成的回复内容

## 配置管理

项目的主要配置信息集中在`application.properties`文件中，包括：

- 服务器配置（端口、错误处理等）
- 文件上传配置（最大文件大小等）
- 日志配置（日志级别、格式等）
- 文件存储路径配置
- 各种API的配置信息（API密钥、端点等）

配置文件采用了环境变量占位符的方式，便于在不同环境中部署和配置。

## 技术栈

- **后端框架**：Spring Boot
- **构建工具**：Maven
- **API调用**：RestTemplate
- **文件存储**：本地文件系统
- **配置管理**：Spring Boot配置机制
- **第三方服务**：百度语音识别API、百度图像分析API、豆包大语言模型API

## 项目特点

1. **模块化设计**：采用分层架构，各模块职责清晰，便于维护和扩展。

2. **可配置性**：大部分配置集中在配置文件中，便于部署和环境切换。

3. **模拟实现**：对于第三方API的调用，提供了模拟实现，便于开发和测试。

4. **统一响应格式**：使用ResponseResult类统一API的返回格式，提高了接口的一致性。

5. **跨域支持**：配置了CORS，支持前端应用从不同域访问后端API。

6. **异常处理**：实现了全局异常处理机制，统一处理各类异常情况。

## 部署与运行

1. 确保已安装Java 17或更高版本
2. 确保已安装Maven
3. 配置`application.properties`中的各项参数，特别是API密钥等信息
4. 使用Maven构建项目：`mvn clean package`
5. 运行生成的jar包：`java -jar target/Child_Agent_back-0.0.1-SNAPSHOT.jar`

## 测试指南

项目包含`API_TEST_GUIDE.md`文件，提供了各API接口的测试方法和示例，包括：
- 测试工具的选择（如curl、Postman等）
- 各接口的测试命令和参数说明
- 预期的响应结果格式

通过这些测试指南，可以方便地验证API接口的功能是否正常。