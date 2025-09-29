import { createStore } from 'vuex'

// 导入各模块
import recorder from './modules/recorder'
import camera from './modules/camera'

const store = createStore({
  modules: {
    recorder,
    camera
  },
  state: {
    // 全局状态
    loading: false,
    error: null
  },
  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    SET_ERROR(state, error) {
      state.error = error
    },
    CLEAR_ERROR(state) {
      state.error = null
    }
  },
  actions: {
    setLoading({ commit }, loading) {
      commit('SET_LOADING', loading)
    },
    setError({ commit }, error) {
      commit('SET_ERROR', error)
    },
    clearError({ commit }) {
      commit('CLEAR_ERROR')
    }
  },
  getters: {
    isLoading: state => state.loading,
    error: state => state.error
  }
})

export default store