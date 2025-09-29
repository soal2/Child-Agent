# 智能助手移动应用

## 项目概述

这是一个基于UniApp框架开发的移动应用，具备录音、对话展示和拍摄功能，最终将导出为安卓应用。

## 功能特性

1. **录音功能**：支持长按录音、松开结束
2. **对话展示**：支持展示多轮对话，区分用户和系统消息
3. **拍摄功能**：点击可调用相机进行拍摄
4. **页面切换**：类似原生Android的Activity/Fragment切换效果
5. **用户体验优化**：操作结果可视化展示、加载状态提示、错误处理与提示机制

## 技术架构

- **框架**：UniApp (Vue3 + Composition API)
- **UI组件库**：uni-ui
- **状态管理**：Vuex
- **构建工具**：HBuilderX
- **目标平台**：Android

## 项目结构

```
src/
├── api/                  # 接口请求封装
├── components/           # 公共组件
│   ├── chat/             # 聊天相关组件
│   ├── recorder/         # 录音相关组件
│   └── camera/           # 拍照相关组件
├── pages/                # 页面目录
│   ├── index/            # 首页
│   ├── chat/             # 聊天页面
│   └── camera/           # 拍照页面
├── store/                # Vuex状态管理
│   ├── modules/          # 模块化状态
│   └── index.js          # 状态管理入口
├── utils/                # 工具函数
├── static/               # 静态资源
└── styles/               # 全局样式
```

## 安装与运行

### 开发环境
1. 安装 HBuilderX 开发工具
2. 安装 Node.js 环境

### 安装依赖
```bash
npm install
```

### 运行项目
在 HBuilderX 中打开项目，点击运行按钮选择对应的设备或模拟器。

## 导出安卓应用

详细导出步骤请参考 [ANDROID_EXPORT.md](ANDROID_EXPORT.md)

## 目录说明

- [TECHNICAL_DOCS.md](TECHNICAL_DOCS.md) - 详细技术方案文档
- [FUNCTION_TEST.md](FUNCTION_TEST.md) - 功能测试说明
- [ANDROID_EXPORT.md](ANDROID_EXPORT.md) - 安卓应用导出指南

## 功能模块

### 1. 首页 (pages/index/index.vue)
- 主功能界面，包含录音、拍照、聊天三个主要功能按钮
- 实时显示对话内容
- 录音状态显示

### 2. 聊天页面 (pages/chat/chat.vue)
- 专门的聊天界面
- 支持文本消息发送
- 对话记录展示

### 3. 拍照页面 (pages/camera/camera.vue)
- 专门的拍照界面
- 照片预览功能
- 照片确认和重拍功能

## 状态管理

使用 Vuex 进行全局状态管理，包含三个模块：
- chat: 聊天相关状态
- recorder: 录音相关状态
- camera: 拍照相关状态

## 权限说明

应用需要以下权限：
- 相机权限：用于拍照功能
- 录音权限：用于录音功能
- 存储权限：用于保存照片和录音文件

## 注意事项

1. 首次运行需要授权相关权限
2. 部分Android设备可能需要手动开启权限
3. 建议在真机上测试以获得最佳体验