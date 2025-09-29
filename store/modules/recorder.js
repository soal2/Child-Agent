const state = {
  isRecording: false,
  recordTime: 0,
  recordTimer: null,
  recordFilePath: '',
  recorderManager: null // 添加录音管理器实例
}

const mutations = {
  SET_RECORDING(state, isRecording) {
    state.isRecording = isRecording
  },
  SET_RECORD_TIME(state, time) {
    state.recordTime = time
  },
  SET_RECORD_TIMER(state, timer) {
    state.recordTimer = timer
  },
  SET_RECORD_FILE_PATH(state, path) {
    state.recordFilePath = path
  },
  SET_RECORDER_MANAGER(state, manager) {
    state.recorderManager = manager
  }
}

const actions = {
  // 初始化录音管理器
  initRecorderManager({ commit, state, dispatch }) {
    if (!state.recorderManager) {
      const recorderManager = uni.getRecorderManager()
      commit('SET_RECORDER_MANAGER', recorderManager)
      
      // 监听录音开始事件
      recorderManager.onStart(() => {
        console.log('录音开始')
      })
      
      // 监听录音结束事件
      recorderManager.onStop((res) => {
        console.log('录音结束', res)
        commit('SET_RECORD_FILE_PATH', res.tempFilePath)
        // 录音结束后自动发送消息
        dispatch('sendVoiceMessage', res)
      })
      
      // 监听录音错误事件
      recorderManager.onError((res) => {
        console.error('录音错误', res)
        uni.showToast({
          title: '录音失败',
          icon: 'none'
        })
      })
    }
  },
  
  startRecording({ commit, state, dispatch }) {
    if (state.isRecording) return
    
    // 如果录音管理器未初始化，则先初始化
    if (!state.recorderManager) {
      dispatch('initRecorderManager')
    }
    
    // 开始录音
    state.recorderManager.start({
      duration: 60000, // 最长录音时长，单位ms
      sampleRate: 16000, // 采样率
      numberOfChannels: 1, // 录音通道数
      encodeBitRate: 96000, // 编码码率
      format: 'mp3', // 音频格式
      frameSize: 50 // 指定帧大小，单位 KB
    })
    
    // 设置录音状态
    commit('SET_RECORDING', true)
    
    // 开始计时
    let time = 0
    const timer = setInterval(() => {
      time++
      commit('SET_RECORD_TIME', time)
    }, 1000)
    
    commit('SET_RECORD_TIMER', timer)
  },
  
  stopRecording({ commit, state }) {
    if (!state.isRecording) return
    
    // 停止录音
    state.recorderManager.stop()
    
    // 清除计时器
    clearInterval(state.recordTimer)
    
    // 更新状态
    commit('SET_RECORDING', false)
    commit('SET_RECORD_TIMER', null)
  },
  
  // 发送语音消息
  sendVoiceMessage({ commit, state, rootState }, recordResult) {
    // 创建语音消息对象
    const voiceMessage = {
      id: Date.now(),
      content: recordResult.tempFilePath, // 语音文件路径
      type: 'user', // 用户消息
      timestamp: new Date().getTime(),
      messageType: 'voice', // 消息类型为语音
      duration: state.recordTime // 语音时长
    }
    
    // 触发全局事件，通知首页组件添加消息
    // 在uni-app中可以通过uni.$emit发送事件
    uni.$emit('addVoiceMessage', voiceMessage)
    
    // 显示录音结果
    uni.showToast({
      title: `录音完成 ${state.recordTime}秒`,
      icon: 'success'
    })
  }
}

const getters = {
  isRecording: state => state.isRecording,
  recordTime: state => state.recordTime,
  recordFilePath: state => state.recordFilePath
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}