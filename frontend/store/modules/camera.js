const state = {
  photoPath: '',
  showPreview: false,
  pendingSend: false // 添加待发送状态
}

const mutations = {
  SET_PHOTO_PATH(state, path) {
    state.photoPath = path
  },
  SET_SHOW_PREVIEW(state, show) {
    state.showPreview = show
  },
  SET_PENDING_SEND(state, pending) {
    state.pendingSend = pending
  }
}

const actions = {
  takePhoto({ commit }) {
    uni.chooseImage({
      count: 1,
      sourceType: ['camera'],
      success: (res) => {
        console.log('拍照成功', res)
        const tempFilePath = res.tempFilePaths[0]
        commit('SET_PHOTO_PATH', tempFilePath)
        commit('SET_SHOW_PREVIEW', true)
        // 设置为待发送状态
        commit('SET_PENDING_SEND', true)
        
        // 触发全局事件，通知首页组件显示待发送状态
        uni.$emit('photoTaken', tempFilePath)
        
        uni.showToast({
          title: '拍照成功',
          icon: 'success'
        })
      },
      fail: (err) => {
        console.error('拍照失败', err)
        uni.showToast({
          title: '拍照失败',
          icon: 'none'
        })
      }
    })
  },
  
  // 取消发送照片
  cancelSend({ commit }) {
    commit('SET_PHOTO_PATH', '')
    commit('SET_SHOW_PREVIEW', false)
    commit('SET_PENDING_SEND', false)
    
    // 触发全局事件，通知首页组件取消发送
    uni.$emit('photoSendCancelled')
  },
  
  // 确认发送照片
  confirmSend({ commit, state }) {
    // 创建图片消息对象
    const imageMessage = {
      id: Date.now(),
      content: state.photoPath,
      type: 'user',
      timestamp: new Date().getTime(),
      messageType: 'image'
    }
    
    // 触发全局事件，通知首页组件发送图片消息
    uni.$emit('sendImageMessage', imageMessage)
    
    // 重置状态
    commit('SET_PHOTO_PATH', '')
    commit('SET_SHOW_PREVIEW', false)
    commit('SET_PENDING_SEND', false)
    
    uni.showToast({
      title: '图片已发送',
      icon: 'success'
    })
  },
  
  // 发送图片和语音组合消息
  sendImageWithVoice({ commit, state }, voiceMessage) {
    // 创建组合消息对象
    const combinedMessage = {
      id: Date.now(),
      content: state.photoPath,
      type: 'user',
      timestamp: new Date().getTime(),
      messageType: 'imageWithVoice',
      voiceContent: voiceMessage.content,
      voiceDuration: voiceMessage.duration
    }
    
    // 触发全局事件，通知首页组件发送组合消息
    uni.$emit('sendCombinedMessage', combinedMessage)
    
    // 重置状态
    commit('SET_PHOTO_PATH', '')
    commit('SET_SHOW_PREVIEW', false)
    commit('SET_PENDING_SEND', false)
    
    uni.showToast({
      title: '图片和语音已发送',
      icon: 'success'
    })
  }
}

const getters = {
  photoPath: state => state.photoPath,
  showPreview: state => state.showPreview,
  pendingSend: state => state.pendingSend
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}