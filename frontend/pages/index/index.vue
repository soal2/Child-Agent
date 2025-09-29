<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar-height"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<text class="nav-title">智能助手</text>
		</view>
		
		<!-- 对话区域 -->
		<scroll-view 
			class="chat-container" 
			scroll-y="true" 
			:scroll-top="scrollTop"
			@scrolltoupper="onScrollToUpper"
			@scrolltolower="onScrollToLower"
		>
			<view class="message-list">
				<view 
					v-for="item in messages" 
					:key="item.id" 
					:class="['message-item', item.type === 'user' ? 'user-message' : 'system-message']"
				>
					<view class="message-content">
						<!-- 文本消息 -->
						<text v-if="!item.messageType || item.messageType === 'text'" class="message-text">{{ item.content }}</text>
						
						<!-- 语音消息 -->
						<view v-if="item.messageType === 'voice'" class="voice-message" @click="playVoice(item.content)">
							<uni-icons type="sound" size="20" color="#fff"></uni-icons>
							<text class="voice-duration">{{ item.duration }}"</text>
						</view>
						
						<!-- 图片消息 -->
						<image v-if="item.messageType === 'image'" :src="item.content" class="image-message" mode="widthFix" @click="previewImage(item.content)"></image>
						
						<!-- 图片和语音组合消息 -->
						<view v-if="item.messageType === 'imageWithVoice'" class="combined-message">
							<image :src="item.content" class="combined-image" mode="widthFix" @click="previewImage(item.content)"></image>
							<view class="voice-message combined-voice" @click="playVoice(item.voiceContent)">
								<uni-icons type="sound" size="20" color="#fff"></uni-icons>
								<text class="voice-duration">{{ item.voiceDuration }}"</text>
							</view>
						</view>
						
						<text class="message-time">{{ formatTime(item.timestamp) }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部功能区域 -->
		<view class="bottom-bar">
			<!-- 功能按钮区域 -->
			<view class="function-buttons">
				<view class="function-btn" @touchstart="startRecording" @touchend="stopRecording">
					<uni-icons 
						type="mic-filled" 
						size="24" 
						:color="isRecording ? '#ff5500' : '#666'"
					></uni-icons>
					<text class="btn-label">{{ isRecording ? '松开结束' : '按住说话' }}</text>
				</view>
				
				<view class="function-btn" @click="takePhoto">
					<uni-icons 
						type="camera-filled" 
						size="24" 
						color="#666"
					></uni-icons>
					<text class="btn-label">拍照</text>
				</view>
			</view>
			
			<!-- 录音时间显示 -->
			<view v-if="isRecording" class="recording-indicator">
				<view class="recording-dot"></view>
				<text class="recording-text">录音中 {{ recordTime }}s</text>
			</view>
			
			<!-- 待发送图片操作区域 -->
			<view v-if="pendingSend" class="pending-send-bar">
				<view class="pending-send-content">
					<image :src="photoPath" class="pending-image-preview" mode="aspectFill"></image>
					<view class="pending-send-actions">
						<button class="action-btn cancel-btn" @click="cancelSend">取消</button>
						<button class="action-btn send-btn" @click="confirmSend">发送</button>
						<button class="action-btn record-btn" @touchstart="startRecordingForImage" @touchend="stopRecordingForImage">录音</button>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 加载提示 -->
		<uni-load-more 
			v-if="isLoading" 
			status="loading" 
			contentText="加载中..."
		></uni-load-more>
		
		<!-- 错误提示 -->
		<uni-popup ref="errorPopup" type="center">
			<view class="error-popup">
				<text class="error-text">{{ error }}</text>
				<button class="error-btn" @click="closeError">确定</button>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import { mapState, mapActions } from 'vuex'
	
	export default {
		data() {
			return {
				scrollTop: 0,
				messages: [],
				innerAudioContext: null, // 音频播放上下文
				pendingVoiceMessage: null // 为图片录制的语音消息
			}
		},
		computed: {
			...mapState('recorder', ['isRecording', 'recordTime', 'recordFilePath']),
			...mapState('camera', ['photoPath', 'showPreview', 'pendingSend']),
			isLoading() {
				return this.loading
			}
		},
		watch: {
			messages: {
				handler() {
					// 新消息到达时滚动到底部
					this.$nextTick(() => {
						this.scrollTop = 999999
					})
				},
				deep: true
			}
		},
		mounted() {
			// 页面加载完成后，初始化录音管理器
			this.initRecorder()
			// 智能体发送开场词
			this.addWelcomeMessage()
			// 监听语音消息事件
			this.listenToVoiceMessage()
			// 监听拍照事件
			this.listenToPhotoEvents()
			// 初始化音频播放上下文
			this.initAudioContext()
		},
		beforeDestroy() {
			// 组件销毁前取消事件监听
			uni.$off('addVoiceMessage')
			uni.$off('photoTaken')
			uni.$off('photoSendCancelled')
			uni.$off('sendImageMessage')
			uni.$off('sendCombinedMessage')
			// 销毁音频播放上下文
			if (this.innerAudioContext) {
				this.innerAudioContext.destroy()
			}
		},
		methods: {
			...mapActions('recorder', ['startRecording', 'stopRecording', 'initRecorderManager']),
			...mapActions('camera', ['takePhoto', 'cancelSend', 'confirmSend', 'sendImageWithVoice']),
			
			formatTime(timestamp) {
				const date = new Date(timestamp)
				return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
			},
			
			onScrollToUpper() {
				// 下拉刷新历史消息
				console.log('下拉刷新')
			},
			
			onScrollToLower() {
				// 上拉加载更多
				console.log('上拉加载更多')
			},
			
			closeError() {
				this.$refs.errorPopup.close()
			},
			
			// 添加系统开场词
			addWelcomeMessage() {
				// 使用setTimeout确保页面完全加载后再添加消息
				setTimeout(() => {
					const welcomeMessage = {
						id: Date.now(),
						content: '你好！我是智能助手，有什么可以帮助你的吗？',
						type: 'system',
						timestamp: new Date().getTime()
					}
					this.messages.push(welcomeMessage)
				}, 500)
			},
			
			// 初始化录音管理器
			initRecorder() {
				this.initRecorderManager()
			},
			
			// 监听语音消息事件
			listenToVoiceMessage() {
				uni.$on('addVoiceMessage', (voiceMessage) => {
					console.log('接收到语音消息', voiceMessage)
					this.messages.push(voiceMessage)
					
					// 模拟智能体回复
					setTimeout(() => {
						const replyMessage = {
							id: Date.now() + 1,
							content: '收到您的语音消息了，我已经理解了您的意思。',
							type: 'system',
							timestamp: new Date().getTime()
						}
						this.messages.push(replyMessage)
					}, 1000)
				})
			},
			
			// 监听拍照事件
			listenToPhotoEvents() {
				// 监听拍照完成事件
				uni.$on('photoTaken', (photoPath) => {
					console.log('拍照完成', photoPath)
					// 这里可以添加拍照完成后的处理逻辑
				})
				
				// 监听取消发送事件
				uni.$on('photoSendCancelled', () => {
					console.log('取消发送照片')
					uni.showToast({
						title: '已取消发送',
						icon: 'none'
					})
				})
				
				// 监听发送图片消息事件
				uni.$on('sendImageMessage', (imageMessage) => {
					console.log('发送图片消息', imageMessage)
					this.messages.push(imageMessage)
					
					// 模拟智能体回复
					setTimeout(() => {
						const replyMessage = {
							id: Date.now() + 1,
							content: '收到您的图片了，我已经保存了。',
							type: 'system',
							timestamp: new Date().getTime()
						}
						this.messages.push(replyMessage)
					}, 1000)
				})
				
				// 监听发送组合消息事件
				uni.$on('sendCombinedMessage', (combinedMessage) => {
					console.log('发送组合消息', combinedMessage)
					this.messages.push(combinedMessage)
					
					// 模拟智能体回复
					setTimeout(() => {
						const replyMessage = {
							id: Date.now() + 1,
							content: '收到您的图片和语音了，我已经理解了您的意思。',
							type: 'system',
							timestamp: new Date().getTime()
						}
						this.messages.push(replyMessage)
					}, 1000)
				})
			},
			
			// 初始化音频播放上下文
			initAudioContext() {
				this.innerAudioContext = uni.createInnerAudioContext()
			},
			
			// 播放语音
			playVoice(filePath) {
				if (this.innerAudioContext) {
					this.innerAudioContext.src = filePath
					this.innerAudioContext.play()
					
					this.innerAudioContext.onPlay(() => {
						console.log('开始播放语音')
					})
					
					this.innerAudioContext.onError((res) => {
						console.log('语音播放失败', res.errMsg)
						uni.showToast({
							title: '语音播放失败',
							icon: 'none'
						})
					})
				}
			},
			
			// 预览图片
			previewImage(imagePath) {
				uni.previewImage({
					urls: [imagePath]
				})
			},
			
			// 取消发送
			cancelSend() {
				this.cancelSend()
			},
			
			// 确认发送
			confirmSend() {
				this.confirmSend()
			},
			
			// 为图片录制语音
			startRecordingForImage() {
				// 为图片录制语音时，先保存当前状态
				this.startRecording()
			},
			
			stopRecordingForImage() {
				// 停止录音并保存语音消息
				this.stopRecording()
				
				// 延迟一段时间确保录音已停止并处理完毕
				setTimeout(() => {
					// 创建语音消息对象
					const voiceMessage = {
						content: this.recordFilePath,
						duration: this.recordTime
					}
					
					// 发送图片和语音组合消息
					this.sendImageWithVoice(voiceMessage)
				}, 500)
			}
		}
	}
</script>

<style lang="scss">
	.container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}
	
	.status-bar-height {
		height: var(--status-bar-height);
		background-color: #fff;
	}
	
	.nav-bar {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 44px;
		background-color: #fff;
		border-bottom: 1px solid #eee;
		position: relative;
		
		.nav-title {
			font-size: 16px;
			font-weight: 500;
			color: #333;
		}
	}
	
	.chat-container {
		flex: 1;
		padding: 10px;
		
		.message-list {
			display: flex;
			flex-direction: column;
			
			.message-item {
				display: flex;
				margin-bottom: 15px;
				
				&.user-message {
					justify-content: flex-end;
					
					.message-content {
						background-color: #007aff;
						color: #fff;
						border-radius: 15px 5px 15px 15px;
					}
				}
				
				&.system-message {
					justify-content: flex-start;
					
					.message-content {
						background-color: #fff;
						color: #333;
						border-radius: 5px 15px 15px 15px;
					}
				}
				
				.message-content {
					max-width: 70%;
					padding: 10px 15px;
					position: relative;
					
					.message-text {
						font-size: 14px;
						line-height: 1.4;
						word-wrap: break-word;
					}
					
					.voice-message {
						display: flex;
						align-items: center;
						min-width: 80px;
						
						.voice-duration {
							margin-left: 5px;
							font-size: 12px;
						}
					}
					
					.image-message {
						max-width: 200px;
						border-radius: 10px;
						cursor: pointer;
					}
					
					.combined-message {
						.combined-image {
							max-width: 200px;
							border-radius: 10px;
							cursor: pointer;
							margin-bottom: 10px;
						}
						
						.combined-voice {
							background-color: rgba(255, 255, 255, 0.2);
							border-radius: 15px;
							padding: 5px 10px;
						}
					}
					
					.message-time {
						display: block;
						font-size: 10px;
						color: #999;
						text-align: right;
						margin-top: 5px;
					}
				}
			}
		}
	}
	
	.bottom-bar {
		background-color: #fff;
		border-top: 1px solid #eee;
		padding: 10px;
		
		.function-buttons {
			display: flex;
			justify-content: space-around;
			align-items: center;
			
			.function-btn {
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				padding: 10px;
				
				.btn-label {
					font-size: 12px;
					color: #666;
					margin-top: 5px;
				}
			}
		}
		
		.recording-indicator {
			display: flex;
			align-items: center;
			justify-content: center;
			margin-top: 10px;
			
			.recording-dot {
				width: 10px;
				height: 10px;
				border-radius: 50%;
				background-color: #ff5500;
				margin-right: 10px;
				animation: blink 1s infinite;
			}
			
			.recording-text {
				font-size: 14px;
				color: #ff5500;
			}
		}
		
		.pending-send-bar {
			margin-top: 10px;
			border-top: 1px solid #eee;
			padding-top: 10px;
			
			.pending-send-content {
				display: flex;
				align-items: center;
				
				.pending-image-preview {
					width: 60px;
					height: 60px;
					border-radius: 5px;
					margin-right: 10px;
				}
				
				.pending-send-actions {
					display: flex;
					flex: 1;
					justify-content: space-around;
					
					.action-btn {
						padding: 5px 10px;
						font-size: 12px;
						border-radius: 5px;
						border: none;
					}
					
					.cancel-btn {
						background-color: #ff3b30;
						color: #fff;
					}
					
					.send-btn {
						background-color: #007aff;
						color: #fff;
					}
					
					.record-btn {
						background-color: #4cd964;
						color: #fff;
					}
				}
			}
		}
	}
	
	@keyframes blink {
		0% { opacity: 1; }
		50% { opacity: 0.5; }
		100% { opacity: 1; }
	}
	
	.error-popup {
		background-color: #fff;
		border-radius: 10px;
		padding: 20px;
		min-width: 250px;
		text-align: center;
		
		.error-text {
			font-size: 14px;
			color: #333;
			margin-bottom: 20px;
			display: block;
		}
		
		.error-btn {
			background-color: #007aff;
			color: #fff;
			border: none;
			border-radius: 5px;
			padding: 8px 20px;
			font-size: 14px;
		}
	}
</style>