# UniApp 移动应用技术方案

## 1. 项目概述

本项目是一个基于UniApp框架开发的移动应用，具备录音、对话展示和拍摄功能，最终将导出为安卓应用。

## 2. 技术选型

- **框架**: UniApp (Vue3 + Composition API)
- **UI组件库**: uni-ui
- **状态管理**: Vuex
- **构建工具**: HBuilderX
- **目标平台**: Android

## 3. 目录结构设计

```
src/
├── api/                  # 接口请求封装
├── components/           # 公共组件
│   ├── recorder/         # 录音相关组件
│   └── camera/           # 拍照相关组件
├── pages/                # 页面目录
│   ├── index/            # 首页
│   └── camera/           # 拍照页面
├── store/                # Vuex状态管理
│   ├── modules/          # 模块化状态
│   └── index.js          # 状态管理入口
├── utils/                # 工具函数
├── static/               # 静态资源
└── styles/               # 全局样式
```

## 4. 核心组件设计

### 4.1 录音组件 (Recorder)
- 支持长按录音、松开结束
- 显示录音时长和状态
- 提供录音波形可视化

### 4.2 对话展示组件 (ChatMessage)
- 区分用户和系统消息
- 支持文本、图片等多种消息类型
- 自动滚动到底部

### 4.3 拍摄组件 (Camera)
- 调用系统相机进行拍照
- 预览拍摄的照片
- 提供重拍和确认功能

## 5. 状态管理方案 (Vuex)

### 5.1 State 设计
```javascript
{
  chat: {
    messages: [],       // 对话记录
    loading: false,     // 加载状态
    error: null         // 错误信息
  },
  recorder: {
    isRecording: false, // 是否正在录音
    recordTime: 0       // 录音时长
  },
  camera: {
    photoPath: '',      // 照片路径
    showPreview: false  // 是否显示预览
  }
}
```

### 5.2 Actions 设计
- sendMessage: 发送消息
- startRecording: 开始录音
- stopRecording: 停止录音
- takePhoto: 拍照
- clearError: 清除错误

## 6. 路由设计

### 6.1 页面路由
- `/pages/index/index`: 首页（主功能页面）
- `/pages/camera/camera`: 拍照页面

### 6.2 页面切换动画
- 使用uni.navigateTo和uni.navigateBack实现页面跳转
- 添加slide-in/slide-out动画效果

## 7. 核心功能实现思路

### 7.1 录音功能
1. 使用`uni.startRecord`开始录音
2. 使用`uni.stopRecord`停止录音
3. 监听录音状态变化，更新UI
4. 处理录音权限申请

### 7.2 拍摄功能
1. 使用`uni.chooseImage`调用相机
2. 获取照片路径并显示预览
3. 处理照片上传或本地保存

### 7.3 对话展示逻辑
1. 使用Vuex管理对话数据
2. 区分用户消息和系统消息样式
3. 实现自动滚动到底部
4. 支持下拉刷新历史消息

## 8. 用户体验优化

### 8.1 加载状态提示
- 请求发送时显示loading
- 操作完成后隐藏loading

### 8.2 错误处理机制
- 捕获并显示操作错误
- 提供重试机制
- 记录错误日志

### 8.3 响应式布局
- 使用rpx单位适配不同屏幕
- 使用flex布局实现自适应
- 考虑横竖屏切换

## 9. 安卓平台兼容性处理

### 9.1 权限申请
- 录音权限: RECORD_AUDIO
- 相机权限: CAMERA
- 存储权限: WRITE_EXTERNAL_STORAGE

### 9.2 设备适配
- 不同分辨率屏幕适配
- Android版本兼容性处理
- 系统UI差异处理